package edu.pitt.pawslab.quizpet.instance;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Assignment.Operator;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.ChildPropertyDescriptor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimplePropertyDescriptor;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class Parser2 {
	private List<String> concepts;
	private List<String> methodNames;
	private List<String> classNames;
	private List<String> imports;
	private ASTNode curNode;
	private CompilationUnit unit;
	private DB db;
	private String question;
	boolean isExample = false;
	boolean isTester = false;
	private ServletContext sv;
	String[] boxingTypes = {"Integer","Float","Short","Double","Long","Byte","Character","Boolean"};

	public void clearLists() {
		if (concepts != null) {
			concepts.clear();
		}
		if (methodNames != null) {
			methodNames.clear();
		}
		if (classNames != null) {
			classNames.clear();
		}		
		if (imports != null) {
			imports.clear();
		}
		unit = null;
		curNode = null;
	}

	private CompilationUnit createAST(String source) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(source.toCharArray());
		parser.setStatementsRecovery(true);		
//		parser.setUnitName(api.Constants.UNIT_NAME);
//		try {
//		    set the project
//			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//			IProject project = root.getProject(api.Constants.PROJECT_NAME);
//			project.open(null /* IProgressMonitor */);
//			IJavaProject javaProject = JavaCore.create(project);
//			parser.setProject(javaProject);			
//		} catch (CoreException e) {
//			e.printStackTrace();
//		}
		return (CompilationUnit) parser.createAST(null);
	}
	
	public void parse(String question,String source,ServletContext sv, boolean isTester) {
		this.sv = sv;
		this.isTester = isTester;
		unit = createAST(source);
		this.question = question;
		this.curNode = unit;
		initializeParserData();
		if (classNames.isEmpty() == false) {
			index(curNode); 
		} 		
		else
		{
			String[] classMethodDefinitionConcepts = { "ClassDefinition","PublicClassSpecifier","MethodDefinition","PublicMethodSpecifier","VoidDataType"};
			String[] classDefinitionConcepts = { "ClassDefinition","PublicClassSpecifier"};
			boolean hasFakeMethodConcept = true;
			if (methodNames.isEmpty())
				source = "public class Tester  { public void test() {  "
						 + source
						 + " }}";
			else {
				source = "public class Tester { " 
						 + source 
						 + " }";
				hasFakeMethodConcept = false;
			}
			unit = createAST(source);
			this.curNode = unit;
			initializeParserData();
			index(curNode);
			// remove the concepts that are related to the fake method/class
			String[] conceptsToBeRemoved;
			if (hasFakeMethodConcept)
				conceptsToBeRemoved = classMethodDefinitionConcepts;
			else
				conceptsToBeRemoved = classDefinitionConcepts;
			removeConcept(conceptsToBeRemoved); 
			//release space
			classMethodDefinitionConcepts = null;
			classDefinitionConcepts = null;
			conceptsToBeRemoved = null;
		}
		
		
		clearParserData();
//		AST ast = unit.getAST();
//		ast.hasResolvedBindings();
//		ast.hasBindingsRecovery();
//		ast.hasStatementsRecovery();
//		List types = unit.types();
//		TypeDeclaration typeDeclaration  = (TypeDeclaration) types.get(0);
//		ITypeBinding binding = typeDeclaration.resolveBinding();
//		assertNotNull("No binding", binding);
//		assertNull("Got a java element", binding.getJavaElement());
//		assertEquals("Wrong name", "p.X", binding.getQualifiedName());
//		methodDeclaration = (MethodDeclaration) typeDeclaration.bodyDeclarations();
//		IMethodBinding methodBinding = methodDeclaration.resolveBinding();
//		assertNotNull("No binding", methodBinding);
//		assertNull("Got a java element", methodBinding.getJavaElement());
//		Block body = methodDeclaration.getBody();
//		VariableDeclarationStatement statement = (VariableDeclarationStatement) body.statements().get(0);
//		VariableDeclarationFragment fragment = (VariableDeclarationFragment) statement.fragments().get(0);
//		IVariableBinding variableBinding = fragment.resolveBinding();
//		assertNotNull("No binding", variableBinding);
//		assertNull("Got a java element", variableBinding.getJavaElement());
//		ExpressionStatement statement2 = (ExpressionStatement) body.statements().get(1);
//		Expression expression = statement2.getExpression();
//		MethodInvocation invocation = (MethodInvocation) expression;
//		Expression expression2 = invocation.getExpression();
//		assertNotNull("No binding", expression2.resolveTypeBinding());
//		FieldDeclaration fieldDeclaration = (FieldDeclaration) typeDeclaration.bodyDeclarations().get(0);
//		VariableDeclarationFragment fragment2 = (VariableDeclarationFragment) fieldDeclaration.fragments().get(0);
//		IVariableBinding variableBinding2 = fragment2.resolveBinding();
//		assertNotNull("No binding", variableBinding2);
//	    assertNull("Got a java element", variableBinding2.getJavaElement());
	}

	private void removeConcept(String[] conceptsToBeRemoved) 
	{
		if (db.isConnectedToWebex21() ) {
			db.deleteConcept(question, conceptsToBeRemoved,isExample);		
		}		
	}

	private void initializeParserData() {
	    db = new DB();
		db.connectToWebex21(sv);
		concepts = new ArrayList<String>();
		methodNames = new ArrayList<String>();
		classNames = new ArrayList<String>();
		imports = new ArrayList<String>();
		TypeDeclaration t;
		FieldDeclaration[] fields;
		MethodDeclaration[] methods;
		// add the different types defined in the given source
		for (Object o : unit.types()) {
			t = (TypeDeclaration) o;
			System.out.println("----------- " + t.getName());
			classNames.add(t.getName().toString());
			// add the methods in each types defined in the given source
			methods = t.getMethods();
			for (MethodDeclaration m : methods) {
				System.out.println("method: " + m.getName());				
				methodNames.add(m.getName().toString());
			}
		}
		// add the imports in the ast tree
		ImportDeclaration temp;
		for (Object i : unit.imports()) {
			temp = (ImportDeclaration) i;
			String imTxt = temp.getName().toString();
			imports.add(imTxt);			
		}	
	}

	private void index(ASTNode node) {

		List<Object> properties = node.structuralPropertiesForType();
		int nodeType = node.getNodeType();
		String nodeClassTxt = node.getClass().getSimpleName();
		int parentType;												
		List<Object> temp;		
		
		for (Iterator<Object> iterator = properties.iterator(); iterator.hasNext();)
		{
			Object descriptor = iterator.next();
		
			if (descriptor instanceof SimplePropertyDescriptor)
            {
				SimplePropertyDescriptor simple = (SimplePropertyDescriptor) descriptor;
				Object value = node.getStructuralProperty(simple);
				//System.out.println("###########################  "+ nodeClassTxt + " parent: "+ node.getParent().getClass().getSimpleName());
//				if (value != null)
//					System.out.println(simple.getId() + " (" + value.toString()+ ") ");
				
				if (isConceptNode(node)) 
				{
					parentType = node.getParent().getNodeType();
					//TODO remove if
					if (node instanceof SimpleName)
					{
						((SimpleName) node).resolveBinding();
						((SimpleName) node).resolveTypeBinding();
					}
										
					/* 
					 * check node type for indexing
					 */
					if (nodeType == ASTNode.STRING_LITERAL)
					{
						addConcept("StringLiteral");
						addConcept("StringDataType");
					}
					
					if (nodeType == ASTNode.TYPE_DECLARATION)
						addConcept("ClassDefinition");
					if (nodeType == ASTNode.ASSIGNMENT)
					{
						Operator op = ((Assignment) node).getOperator();
						if (op == Assignment.Operator.ASSIGN)
							addConcept("SimpleAssignmentExpression");
						else if (op == Assignment.Operator.PLUS_ASSIGN)
							addConcept("AddAssignmentExpression");
						else if (op == Assignment.Operator.TIMES_ASSIGN)
							addConcept("MultiplyAssignmentExpression");
						else if (op == Assignment.Operator.MINUS_ASSIGN)
							addConcept("MinusAssignmentExpression");
						else if (op == Assignment.Operator.DIVIDE_ASSIGN)
							addConcept("DivideAssignmentExpression");
					}
					
					if (nodeType == ASTNode.INFIX_EXPRESSION)
					{
						InfixExpression infixEx = (InfixExpression)node;
						Expression leftOperand = infixEx.getLeftOperand();
						Expression rightOperad = infixEx.getRightOperand();
						
						if (leftOperand instanceof ClassInstanceCreation)
						{
							ClassInstanceCreation ci = (ClassInstanceCreation) leftOperand;
							if (isAutoBoxingType(ci.getType()))
								addConcept("AutoBoxing");							
						}
						if (rightOperad instanceof ClassInstanceCreation)
						{
							ClassInstanceCreation ci = (ClassInstanceCreation) rightOperad;
							if (isAutoBoxingType(ci.getType()))
								addConcept("AutoBoxing");							
						}

						//determine the expression type
						if (infixEx.getOperator() == InfixExpression.Operator.AND | infixEx.getOperator() == InfixExpression.Operator.CONDITIONAL_AND)
							addConcept("AndExpression");
						else if (infixEx.getOperator() == InfixExpression.Operator.DIVIDE)
							addConcept("DivideExpression");
						else if (infixEx.getOperator() == InfixExpression.Operator.EQUALS)
							addConcept("EqualExpression");
						else if (infixEx.getOperator() == InfixExpression.Operator.GREATER)
							addConcept("GreaterExpression");
						else if (infixEx.getOperator() == InfixExpression.Operator.GREATER_EQUALS)
							addConcept("GreaterEqualExpression");	
						else if (infixEx.getOperator() == InfixExpression.Operator.LESS)
							addConcept("LessExpression");	
						else if (infixEx.getOperator() == InfixExpression.Operator.LESS_EQUALS)
							addConcept("LessEqualExpression");	
						else if (infixEx.getOperator() == InfixExpression.Operator.MINUS)
							addConcept("SubtractExpression");	
						else if (infixEx.getOperator() == InfixExpression.Operator.NOT_EQUALS)
							addConcept("NotEqualExpression");	
						else if (infixEx.getOperator() == InfixExpression.Operator.OR | infixEx.getOperator() == InfixExpression.Operator.CONDITIONAL_OR)
							addConcept("OrExpression");	
						else if (infixEx.getOperator() == InfixExpression.Operator.PLUS)
						{
							if( (leftOperand instanceof StringLiteral | rightOperad instanceof StringLiteral) )
							{
								addConcept("StringAddition");	
								addConcept("StringDataType");								
							}
							else
								addConcept("AddExpression");
						}
						else if (infixEx.getOperator() == InfixExpression.Operator.REMAINDER)
							addConcept("ModulusExpression");
						else if (infixEx.getOperator() == InfixExpression.Operator.TIMES)
							addConcept("MultiplyExpression");
						else if (infixEx.getOperator() == InfixExpression.Operator.XOR)
							addConcept("XORExpression");
						else if (infixEx.getOperator() == InfixExpression.Operator.LEFT_SHIFT)
							addConcept("LeftShiftExpression");
						else if (infixEx.getOperator() == InfixExpression.Operator.RIGHT_SHIFT_SIGNED)
							addConcept("RightShiftSignedExpression");
						else if (infixEx.getOperator() == InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED)
							addConcept("RightShiftUnsignedExpression");
						
						//check for null operand
						if (rightOperad instanceof NullLiteral | leftOperand instanceof NullLiteral)
							addConcept("null");
						//check for the constantInvocation concept
						if ( leftOperand instanceof QualifiedName)
						{
							if (((QualifiedName) leftOperand).getName().getIdentifier().equals("length") == false)
								addConcept("ConstantInvocation");
						}
						
						if ( rightOperad instanceof QualifiedName)
						{
							if (((QualifiedName) rightOperad).getName().getIdentifier().equals("length") == false)
								addConcept("ConstantInvocation");
						}
						
						if ( leftOperand instanceof CastExpression | rightOperad instanceof CastExpression)
						{
							if ( ((CastExpression) leftOperand).getExpression() instanceof QualifiedName)							
								addConcept("ConstantInvocation");							
						}
						
						//add String addition concept
//						if (value.toString().equals("+"))
//						{
//							if ( leftOperand instanceof SimpleName & rightOperad instanceof SimpleName)
//							{								
//								if (isStringOperand(((SimpleName) leftOperand)) & isStringOperand(((SimpleName) rightOperad)))									 
//								{
//									addConcept("StringAddition");
//								}	
//							}	
//							ITypeBinding itb = leftOperand.;
//							System.out.println(itb);
//						}
						//add object equality concept
//						if (value.toString().equals("=="))
//						{
//							if ( leftOperand instanceof SimpleName & rightOperad instanceof SimpleName)
//							{						
//								if (isObjectOperand(((SimpleName) leftOperand)) & isObjectOperand(((SimpleName) rightOperad)))									 
//								{
//									addConcept("ObjectEquality");
//								}	
//							}	
//						}
					}						
					
					else if (nodeType == ASTNode.POSTFIX_EXPRESSION)
					{
						PostfixExpression pfexp = (PostfixExpression) node;
						if (pfexp.getOperator() == PostfixExpression.Operator.INCREMENT)
							addConcept("PostIncrementExpression");
						else if (pfexp.getOperator() == PostfixExpression.Operator.DECREMENT)
							addConcept("PostDecrementExpression");
					}
					
					else if (nodeType == ASTNode.PREFIX_EXPRESSION)
					{
						PrefixExpression pfexp = (PrefixExpression) node;
						if (pfexp.getOperator() == PrefixExpression.Operator.INCREMENT)
							addConcept("PreIncrementExpression");
						else if (pfexp.getOperator() == PrefixExpression.Operator.DECREMENT)
							addConcept("PreDecrementExpression");
						else if (pfexp.getOperator() == PrefixExpression.Operator.NOT)
							addConcept("NotExpression");
					}
					
					else if (nodeType == ASTNode.BOOLEAN_LITERAL)
					{
						if (((BooleanLiteral) node).booleanValue() == true)
							addConcept("True");
						else 
							addConcept("False");

					}
							
					else if (nodeType == ASTNode.METHOD_DECLARATION)
					{
						MethodDeclaration md = (MethodDeclaration) node;	
						List<Modifier> modifiers = md.modifiers();
						if (md.getBody() != null) // is not interface definition
						{
							List<Statement> list = md.getBody().statements();
							//check for formal method parameter concept
							if (md.parameters().isEmpty() == false)
								addConcept("FormalMethodParameter");												
							if (md.isConstructor())
							{
								addConcept("ConstructorDefinition");	
								for (Modifier m : modifiers)
								{
									if(m.isPrivate())
										addConcept("PrivateConstructorSpecifier");
									else if(m.isPublic())
										addConcept("PublicConstructorSpecifier");									
									else if (m.isProtected())
										addConcept("ProtectedConstructorSpecifier");																
								}
							}
							else 
							{
								addConcept("MethodDefinition");
								for (Modifier m : modifiers)
								{
									if (m.isFinal())
										addConcept("FinalMethodSpecifier");
									else if (m.isStatic())
										addConcept("StaticMethodSpecifier");							
									else if(m.isPrivate())
										addConcept("PrivateMethodSpecifier");
									else if(m.isPublic())
										addConcept("PublicMethodSpecifier");
									else if (m.isAbstract())
									{
										addConcept("AbstractMethodDefinition");
										addConcept("AbstractMethodSpecifier");
									}
									else if (m.isNative())
										addConcept("NativeMethodSpecifier");
									else if (m.isProtected())
										addConcept("ProtectedMethodSpecifier");
									else if (m.isSynchronized())
										addConcept("SynchronizedMethodSpecifier");
									else if (m.isStrictfp())
										addConcept("StrictfpMethodSpecifier");								
								}
							}
							/*Remark: since the concepts added in the for loop can not be accessed directly
							 * they must be called on addConcept(string,int,int) to point to the correct line							  
							 */
							int startPosition;
							int length;
							for (Statement s : list)
							{
								startPosition = s.getStartPosition();
								length = s.getLength();
								if (s instanceof SuperConstructorInvocation)
									addConcept("SuperclassConstructorCall",startPosition,length);
								else if (s instanceof ExpressionStatement)
								{
									if (((ExpressionStatement) s).getExpression() instanceof SuperMethodInvocation)
									{
										addConcept("SuperclassMethodCall",startPosition,length);
										addConcept("SuperReference",startPosition,length);
									}
								}
								else if (s instanceof ReturnStatement)
								{
									Expression e = ((ReturnStatement) s).getExpression();
									//check for super reference and super method call in return statement
									if (e instanceof SuperMethodInvocation)
									{
										addConcept("SuperclassMethodCall",startPosition,length);
										addConcept("SuperReference",startPosition,length);
									}
									else if (e instanceof InfixExpression)
									{
										InfixExpression ie = (InfixExpression) e;
										if (ie.getLeftOperand() instanceof SuperMethodInvocation | ie.getRightOperand() instanceof SuperMethodInvocation)
										{
											addConcept("SuperclassMethodCall",startPosition,length);
											addConcept("SuperReference",startPosition,length);
										}
									}
								}
							}	
						}	
						if (md.getParent() instanceof TypeDeclaration)
						{
							TypeDeclaration td = (TypeDeclaration) md.getParent();
							if (td.isInterface())
							{
								addConcept("AbstractMethodDefinition");
							}
						}
						//check overriding String.toString() and String.equals() 
						if (isOverridingEquals(md))
							addConcept("OverridingEquals");						
						if (isOverridingToString(md))
							addConcept("OverridingToString");											
						if(md.thrownExceptions().isEmpty() == false)
						{
							addConcept("ThrowsSpecification");
						}
						if(md.getReturnType2() != null)
							if(md.getReturnType2().toString().equals("String"))		
								addConcept("StringDataType");						
					}					
					else if (nodeType == ASTNode.IMPORT_DECLARATION)
					{
						ImportDeclaration imd = (ImportDeclaration) node;
						String importName = imd.getName().getFullyQualifiedName();
						if (importName.equals("java.util.ArrayList"))
							addConcept("java.util.ArrayList");
						addConcept("ImportStatement");
						if (simple.getId().equals("static")	& value.toString().equals("true"))
							addConcept("StaticImport");
						else if (simple.getId().equals("onDemand") & value.toString().equals("true"))
							addConcept("OnDemandImport");
					}										
							
					else if (nodeType == ASTNode.TYPE_DECLARATION)
					{
						TypeDeclaration td = (TypeDeclaration) node;
						List<Modifier> modifiers = td.modifiers();

						if (td.isInterface())
						{
							addConcept("InterfaceDefinition");
							for (Modifier m : modifiers)
							{
								if (m.isStatic())
									addConcept("StaticInterfaceSpecifier");							
								else if(m.isPrivate())
									addConcept("PrivateInterfaceSpecifier");
								else if(m.isPublic())
									addConcept("PublicInterfaceSpecifier");	
								else if (m.isAbstract())
									addConcept("AbstractInterfaceSpecifier");	
								else if (m.isProtected())
									addConcept("ProtectedInterfaceSpecifier");	
								else if (m.isStrictfp())
									addConcept("StrictfpInterfaceSpecifier");
							}
						}
						else
						{
							for (Modifier m : modifiers)
							{
								if (m.isFinal())
									addConcept("FinalClassSpecifier");
								else if (m.isStatic())
									addConcept("StaticClassSpecifier");							
								else if(m.isPrivate())
									addConcept("PrivateClassSpecifier");
								else if(m.isPublic())
									addConcept("PublicClassSpecifier");	
								else if (m.isAbstract())
									addConcept("AbstractClassSpecifier");	
								else if (m.isProtected())
									addConcept("ProtectedClassSpecifier");	
								else if (m.isStrictfp())
									addConcept("StrictfpClassSpecifier");
							}	
						}					
					}
					
					else if (nodeType == ASTNode.VARIABLE_DECLARATION_FRAGMENT)
					{
						VariableDeclarationFragment vd = (VariableDeclarationFragment) node;
						if (vd.getParent().toString().contains("ArrayList"))
							addConcept("java.util.ArrayList");

						if (vd.getInitializer() == null)
						addConcept("SimpleVariable");
						else 
							addConcept("SimpleAssignmentExpression");
					}
					else if (nodeType == ASTNode.PRIMITIVE_TYPE)
						addPrimitiveTypeConcept((PrimitiveType) node);
				}
			} 
			
			
			else if (descriptor instanceof ChildPropertyDescriptor)
			{
				ChildPropertyDescriptor child = (ChildPropertyDescriptor) descriptor;
				ASTNode childNode = (ASTNode) node.getStructuralProperty(child);

				if (childNode != null)
				{						
					/*
					 * check id for indexing
					 */
					
					if (child.getId().equals("body"))
					{
						if (nodeType == ASTNode.TRY_STATEMENT)
						{
							addConcept("TryCatchStatement");
							//addConcept("StatementBlock"); //trystatement always has block
						}
					}					
					else if (child.getId().equals("qualifier"))
					{
						if (nodeType == ASTNode.QUALIFIED_NAME)
						{
							QualifiedName q = (QualifiedName) node;
							if (q.getQualifier().toString().equals("Math"))
							{								
								//checking constant calls in Math
								if (q.getName().getIdentifier().equals("PI") | 
										q.getName().getIdentifier().equals("E"))
								{
									addConcept("Constant");
									addConcept("ConstantInvocation");
								}
							}
						}
					}
					else if (child.getId().equals("superclassType"))
					{
						if (nodeType == ASTNode.TYPE_DECLARATION)
							addConcept("ExtendsSpecification");
					}
					
					else if (child.getId().equals("expression"))
					{
						if (node instanceof ReturnStatement)						
							addConcept("ReturnStatement");
						
						else if (node instanceof CastExpression)
							addConcept("ExplicitTypeCasting");
						
						Statement body = null;
						Statement elseStatement = null;
						if (node instanceof ForStatement)
						{
							addConcept("ForStatement");
							body = ((ForStatement) node).getBody();
						}
						else if (node instanceof EnhancedForStatement)
						{
							addConcept("ForEachStatement");
							body = ((EnhancedForStatement) node).getBody();
						}
						else if (node instanceof WhileStatement)
						{
							addConcept("WhileStatement");
							body = ((WhileStatement) node).getBody();
						}
						else if (node instanceof DoStatement)
						{
							addConcept("DoStatement");
							addConcept("WhileStatement");
							DoStatement ds = (DoStatement) node;
							body = ds.getBody();
						}
						else if (node instanceof SwitchStatement)
						{
							addConcept("SwitchStatement");
							//addConcept("StatementBlock"); //switch always has block
							List<Statement> stats = ((SwitchStatement) node).statements();
							
							int startPosition;
							int length;
							for (Statement s : stats)
							{
								startPosition = s.getStartPosition();
								length = s.getLength();
//								if (s instanceof Statement)
//									if (isCommonStatement((Statement)s))
//										addConcept("NestedStatement",startPosition,length);								
								if (s instanceof SwitchCase)
								{
									SwitchCase sc = (SwitchCase) s;
									if (sc.isDefault())
										addConcept("DefaultClause",startPosition,length);
									else 
										addConcept("CaseClause",startPosition,length);
								}
								else if (s instanceof BreakStatement)
									addConcept("BreakStatement",startPosition,length);
							}
						}
						else if (node instanceof TryStatement)
						{
							addConcept("TryCatchStatement");
							//addConcept("StatementBlock"); //trystatement always has block
							body = ((TryStatement) node).getBody();
						}						
						else if (node instanceof IfStatement)	
						{
							IfStatement ifs = (IfStatement) node;
							body = ifs.getThenStatement();
							if (ifs.getElseStatement() == null)
								addConcept("IfStatement");	
							else if (ifs.getElseStatement() instanceof IfStatement)
								addConcept("IfElseIfStatement");	
							else
							{
								addConcept("IfElseStatement");	
								elseStatement = ifs.getElseStatement();
							}
						}
						else if (node instanceof ThrowStatement)
						{
							addConcept("ThrowStatement");						
						}
						if (body != null)
						{
//							if (body instanceof Block)
//								addConcept("StatementBlock",body.getStartPosition(),body.getLength());
							Statement nestedStatement = getNestedStatement(body);
							if (nestedStatement != null)
							{
								if (nestedStatement instanceof ForStatement)
								{
									if (nestedStatement.getParent() instanceof Block)
									{
										List<Statement> stats = ((Block) nestedStatement.getParent()).statements();
										for (Statement s : stats)
											if (s instanceof ForStatement)
												addConcept("NestedForLoops",nestedStatement.getParent().getStartPosition(),nestedStatement.getParent().getLength());	
									}
									else if (nestedStatement.getParent() instanceof ForStatement)
										addConcept("NestedForLoops",nestedStatement.getParent().getStartPosition(),nestedStatement.getParent().getLength());	
								}								
							}
						}
//						if (elseStatement != null)
//						{
////							if (elseStatement instanceof Block)
////								addConcept("StatementBlock",elseStatement.getStartPosition(),elseStatement.getLength());
//							Statement nestedStatement = getNestedStatement(elseStatement);
//							if (nestedStatement != null)
//								addConcept("NestedStatement",nestedStatement.getStartPosition(),nestedStatement.getLength());	
//						}
					}				
					
					else if (child.getId().equals("array"))
					{
						if (node instanceof ArrayAccess)
						addConcept("ArrayElement");
						
					}
					/*
					 * check node type for indexing
					 */
					if (nodeType == ASTNode.SUPER_FIELD_ACCESS)
						addConcept("SuperReference");
					else if (nodeType == ASTNode.FIELD_ACCESS)
					{
						addConcept("InstanceFieldInvocation");
						FieldAccess fa = (FieldAccess) node; 
						if (fa.getExpression() instanceof ThisExpression)
						{
							addConcept("ThisReference");
						}
					}
					else if(nodeType == ASTNode.FIELD_DECLARATION)
					{
						boolean isStatic = false;
						boolean isFinal = false;
						FieldDeclaration fd = (FieldDeclaration) node;
						List<Modifier> modifiers = fd.modifiers();
					
						if (fd.getType().isParameterizedType())
						checkAndAddStringParametrizedType((ParameterizedType)fd.getType());
						checkAndAddStringVariableConcept(fd.getType());
						if (fd.getType().isArrayType())
						{
							if (((ArrayType) fd.getType()).getDimensions() > 1)
								addConcept("MultiDimensionalArrayDataType");
							else
								addConcept("ArrayDataType");
						}
						
						for (Modifier m : modifiers)
						{
							if (m.isStatic())	
							{
								isStatic = true;
								addConcept("StaticFieldSpecifier");
							}
							if (m.isFinal())
							{
								isFinal = true;	
								addConcept("FinalFieldSpecifier");
							}
							if (m.isPrivate())
								addConcept("PrivateFieldSpecifier");
							if (m.isProtected())
								addConcept("ProtectedFieldSpecifier");
							if (m.isPublic())
								addConcept("PublicFieldSpecifier");
							if (m.isTransient())
								addConcept("TransientFieldSpecifier");
							if (m.isVolatile())
								addConcept("VolatileFieldSpecifier");
						}
						
						if (isStatic)					
							addConcept("ClassField");						
						else
						{
							addConcept("InstanceField");
							//addConcept("InstanceFieldDefinitionStatement"); 
						}						
						
						for (Object x : fd.fragments())
						{
								if (x instanceof VariableDeclarationFragment)
								{
									if (((VariableDeclarationFragment) x).getInitializer() != null)
									{								    
										if (isFinal && isStatic)
											addConcept("ClassConstantInitializationStatement");
										else									
									    	addConcept("InstanceFieldInitializationStatement");									
									}
								}						
						}
					}				
					
					else if (nodeType == ASTNode.VARIABLE_DECLARATION_STATEMENT)
					{
						VariableDeclarationStatement vs = (VariableDeclarationStatement) node;
						List<VariableDeclarationFragment> fragments = vs.fragments();
						List<Modifier> modifiers = vs.modifiers();
						for (Modifier m : modifiers)
						{
							if (m.isFinal())
							{
								addConcept("Constant");
								for (VariableDeclarationFragment f : fragments)
									if (f.getInitializer() != null)
										addConcept("ConstantInitializationStatement");
							}							
						}
						for (VariableDeclarationFragment vf : fragments)
						{
							//check for null concept
							if (vf.getInitializer() instanceof NullLiteral)
								addConcept("nullInitialization");
							else if (vf.getInitializer() instanceof ArrayInitializer)									
							{
//								if (vs.getType().isArrayType())
//								{
//									if (((ArrayType) vs.getType()).getDimensions() > 1)
//									{
//										 addConcept("MultiDimensionalArrayInitializationStatement");
//										 addConcept("MultiDimensionalArrayInitializer");
//									     addConcept("MultiDimensionalArrayVariable");
//									}
//									else
								//	{
//										  addConcept("ArrayInitializationStatement");
//										  addConcept("ArrayInitializer");
//									      addConcept("ArrayVariable"); //this is to catch array variables like int/String/.. array[] = {....}									
//								//	}	
							//	}
							//	else
								//{
									addConcept("ArrayInitializationStatement");
									  addConcept("ArrayInitializer");
								      addConcept("ArrayVariable"); //this is to catch array variables like int/String/.. array[] = {....}
								//}
							}
							else if (vf.getInitializer() instanceof ArrayCreation)
							{
//								if (vs.getType().isArrayType())
//								{
////									if (((ArrayType) vs.getType()).getDimensions() > 1)
//									{
//										 addConcept("MultiDimensionalArrayCreationStatement");
//									     addConcept("MultiDimensionalArrayVariable"); 									
//									}
//									else
//									{
//										addConcept("ArrayCreationStatement");	
//									    addConcept("ArrayVariable"); //this is to catch array variables like int/String/.. array[] = new int[]			
//										//addConcept("ConstructorCall");
//									}		
//								}
//								else
//								{
									addConcept("ArrayCreationStatement");
									addConcept("ArrayVariable"); //this is to catch array variables like int/String/.. array[] = {....}
//								}
							}
							else if (vf.getInitializer() instanceof StringLiteral)
								  addConcept("StringInitializationStatement");
							
							//else
								//addConcept("VariableInitializationStatement");

							if (vf.getInitializer() instanceof NumberLiteral 
									| vf.getInitializer() instanceof BooleanLiteral
									| vf.getInitializer() instanceof CharacterLiteral)
							{
							    if (isAutoBoxingType(vs.getType()))
										  addConcept("AutoBoxing");
								
							}
						}
						checkAndAddObjectVariableConcept(vs.getType());
						
						checkAndAddStringVariableConcept(vs.getType());
						//primitive datatype concepts
						if (vs.getType().isPrimitiveType())
						{
							PrimitiveType pType = (PrimitiveType) vs.getType();
							addPrimitiveTypeConcept(pType);							
						}
						else if(vs.getType().isSimpleType() | vs.getType().isQualifiedType())
						{
							checkAndAddWrapperClassTypes(vs.getType());							
							if(vs.getType().toString().equals("String") == false)
							{
								for (VariableDeclarationFragment f : fragments)
									if (f.getInitializer() == null)
										addConcept("ObjectVariable");	
							}
						}
						else if (vs.getType().isArrayType())
						{
							
							if (((ArrayType) vs.getType()).getDimensions() > 1)
								addConcept("MultiDimensionalArrayDataType");
							else
								addConcept("ArrayDataType");
						}
						else if (vs.getType().isParameterizedType())
							checkAndAddStringParametrizedType((ParameterizedType) vs.getType());
					}
																
					else if (nodeType == ASTNode.METHOD_INVOCATION)
					{
						boolean isGeneral = true;
						MethodInvocation mi = (MethodInvocation) node;
						checkAndAddStringMethods(mi.getName().getIdentifier());

						Expression mexp = mi.getExpression();
						if (mi.getName().getIdentifier().equals("length") && mi.arguments().isEmpty())
						{
							addConcept("java.lang.String.length");
							isGeneral = false;
						}
					    if (concepts.contains("java.util.ArrayList"))
						{	
					    	
							if (mi.getName().getIdentifier().equals("size") && mi.arguments().isEmpty())
							{
								addConcept("java.util.ArrayList.size");
								isGeneral = false;
							}
							else if (mi.getName().getIdentifier().equals("add"))
							{
								addConcept("java.util.ArrayList.add");
								isGeneral = false;
							}
							else if (mi.getName().getIdentifier().equals("get"))
							{
								addConcept("java.util.ArrayList.get");
								isGeneral = false;
							}
							else if (mi.getName().getIdentifier().equals("remove"))
							{
								addConcept("java.util.ArrayList.remove");
								isGeneral = false;
							}
							else if (mi.getName().getIdentifier().equals("set"))
							{
								addConcept("java.util.ArrayList.set");
								isGeneral = false;
							}
						}
						if (mexp instanceof StringLiteral)
						{
							isGeneral = false;

							addConcept("StringDataType");
							addConcept("StringLiteralMethodInvocation");
							if (mi.getName().getIdentifier().equals("substring"))
								addConcept("java.lang.String.substring");
						}
						
						
     					if (mexp instanceof QualifiedName)
						{
							
							QualifiedName mq = (QualifiedName) mexp;
							if (mq.toString().equals("System.out"))
							{
								isGeneral = false;
								if (mi.getName().toString().equals("println"))
									addConcept("java.lang.System.out.println");
								else if (mi.getName().toString().equals("print"))
									addConcept("java.lang.System.out.print");								
							}
						}
						else if (mexp instanceof SimpleName)
						{
							
							if (((SimpleName) mexp).getIdentifier().equals("Double") && mi.getName().getIdentifier().equals("parseDouble"))
							{	
								addConcept("java.lang.Double.parseDouble");	
								isGeneral = false;
							}
							else if (((SimpleName) mexp).getIdentifier().equals("Integer") && mi.getName().getIdentifier().equals("parseInt"))
							{
								addConcept("java.lang.Integer.parseInt");		
								isGeneral = false;
							}
							else if (((SimpleName) mexp).getIdentifier().equals("Math"))
							{
								isGeneral = false;
								if (mi.getName().getIdentifier().equals("pow"))
									addConcept("java.lang.Math.pow");
								else if (mi.getName().getIdentifier().equals("round"))
									addConcept("java.lang.Math.round");
								else if (mi.getName().getIdentifier().equals("sqrt"))
									addConcept("java.lang.Math.sqrt");
								else if (mi.getName().getIdentifier().equals("ceil"))
									addConcept("java.lang.Math.ceil");
								else if (mi.getName().getIdentifier().equals("abs"))
									addConcept("java.lang.Math.abs");
								else if (mi.getName().getIdentifier().equals("max"))
									addConcept("java.lang.Math.max");
								else if (mi.getName().getIdentifier().equals("min"))
									addConcept("java.lang.Math.min");
								else if (mi.getName().getIdentifier().equals("floor"))
									addConcept("java.lang.Math.floor");
							}							
								
						}
						if (mexp != null && (mexp instanceof StringLiteral == false))
						{								
							if (isGeneral)
								addConcept("ObjectMethodInvocation");
						}
						if (mi.arguments().isEmpty() == false)
							addConcept("ActualMethodParameter");
					}
					
					//check for null return concept
					else if (nodeType == ASTNode.RETURN_STATEMENT)
					{
						ReturnStatement rs = (ReturnStatement) node;
//						if (rs.getExpression() instanceof NullLiteral)
//							addConcept("nullReturn");
						if (rs.getExpression() instanceof StringLiteral)
						{
							addConcept("StringLiteral");
							addConcept("StringDataType");
						}
						if (rs.getExpression() instanceof ClassInstanceCreation)
						{
							ClassInstanceCreation ci = (ClassInstanceCreation) rs.getExpression();
							checkAndAddWrapperClassTypes(ci.getType());
						}	
					}
					
					else if (nodeType == ASTNode.QUALIFIED_NAME)
					{
						if (((QualifiedName) node).getName().getIdentifier().equals("length"))
							addConcept("ArrayLength");
					}
					else if (nodeType == ASTNode.CLASS_INSTANCE_CREATION)
					{
						ClassInstanceCreation ci = (ClassInstanceCreation) node;
						checkAndAddWrapperClassTypes(ci.getType());
						if (ci.getType().isParameterizedType())
							addConcept("GenericObjectCreationStatement");
						else if (ci.getType().isSimpleType())
						{							
							if (ci.getType().toString().equals("String"))
							{
								addConcept("StringCreationStatement");
								addConcept("StringConstructorCall");	
							}
							else if (isAutoBoxingType(ci.getType()) == false)
							{
								addConcept("ObjectCreationStatement");
								addConcept("ConstructorCall");
							}
							else 
							{
								addConcept("WrapperClassCreationStatement");
								addConcept("WrapperClassConstructorCall");
							}
						}
						if (ci.arguments().isEmpty() == false)
							addConcept("ActualMethodParameter");
						
					}	
					else if (nodeType == ASTNode.SINGLE_VARIABLE_DECLARATION)
					{
						SingleVariableDeclaration sv = (SingleVariableDeclaration) node;
						if (sv.getType().toString().equals("Object"))
							addConcept("java.lang.Object");
						checkAndAddWrapperClassTypes(sv.getType());
							
					}	
					else if (nodeType == ASTNode.TYPE_DECLARATION)
					{
						TypeDeclaration td = (TypeDeclaration) node;
						if (td.getName().getIdentifier().toLowerCase().contains("exception"))
							addConcept("ExceptionClass");
					}
					//System.out.println("Child (" + child.getId() + " -- "+ nodeClassTxt + ") {");
					curNode = childNode;
					index(childNode);
					//System.out.println("}");
				}				
			}			
			
			else {
				ChildListPropertyDescriptor list = (ChildListPropertyDescriptor) descriptor;
				//System.out.println("List (" + list.getId() + ") {");				
				temp = (List<Object>) node.getStructuralProperty(list);
				
				/*
				 * check id for indexing
				 */
			    if (list.getId().equals("superInterfaceTypes"))
				{
					if (temp.isEmpty() == false)
					{
						addConcept("ImplementsSpecification");
						addConcept("MethodImplementation");
					}
				} 			
				
				if (temp.isEmpty() == false)
				{
					for (Iterator<Object> itr = temp.iterator(); itr.hasNext();) {
						curNode = (ASTNode) itr.next();
						index(curNode);
					}
				}					
								
				//System.out.println("}");
			}
		}
	}

    /*
     * this method just add concepts based on guess. User confirmation is necessary.
     */
	private void checkAndAddStringMethods(String name) {

		if (name.equals("charAt"))
			addConcept("java.lang.String.charAt");
		else if (name.equals("equals"))
			addConcept("java.lang.String.equals");
		else if (name.equals("equalsIgnoreCase"))
			addConcept("java.lang.String.equalsIgnoreCase");
		else if (name.equals("replace"))
				addConcept("java.lang.String.replace");
		else if (name.equals("substring"))
			addConcept("java.lang.String.substring");
		
	}

	private void checkAndAddWrapperClassTypes(Type type) {
		if (type.toString().equals("Double"))
			addConcept("java.lang.Double");
		else if (type.toString().equals("Integer"))
			addConcept("java.lang.Integer");
		else if (type.toString().equals("Float"))
			addConcept("java.lang.Float");
		else if (type.toString().equals("Short"))
			addConcept("java.lang.Short");
		else if (type.toString().equals("Long"))
			addConcept("java.lang.Long");
		else if (type.toString().equals("Byte"))
			addConcept("java.lang.Byte");
		else if (type.toString().equals("Character"))
			addConcept("java.lang.Character");
		else if (type.toString().equals("Boolean"))
			addConcept("java.lang.Boolean");		
	}

	private void addPrimitiveTypeConcept(PrimitiveType pType) {
		if (pType.getPrimitiveTypeCode() ==  PrimitiveType.BOOLEAN)
			addConcept("BooleanDataType");
		else if (pType.getPrimitiveTypeCode() ==  PrimitiveType.BYTE)
			addConcept("ByteDataType");
		else if (pType.getPrimitiveTypeCode() ==  PrimitiveType.CHAR)
			addConcept("CharDataType");
		else if (pType.getPrimitiveTypeCode() ==  PrimitiveType.DOUBLE)
			addConcept("DoubleDataType");
		else if (pType.getPrimitiveTypeCode() ==  PrimitiveType.FLOAT)
			addConcept("FloatDataType");
		else if (pType.getPrimitiveTypeCode() ==  PrimitiveType.INT)
			addConcept("IntDataType");
		else if (pType.getPrimitiveTypeCode() ==  PrimitiveType.LONG)
			addConcept("LongDataType");
		else if (pType.getPrimitiveTypeCode() ==  PrimitiveType.SHORT)
			addConcept("ShortDataType");
		else if (pType.getPrimitiveTypeCode() ==  PrimitiveType.VOID)
			addConcept("VoidDataType");
	}

	private Statement getNestedStatement(Statement body)
	{
		if (body instanceof Block)
		{
			List<Statement> statements = ((Block) body).statements();
			for (Statement s : statements)
			{
				if (isCommonStatement(s))
					return s;
			}			
		}
		else if (isCommonStatement(body))
			return body;
		return null;
	}

	private boolean isCommonStatement(Statement s) 
	{
     		if (s instanceof DoStatement
			   | s instanceof EnhancedForStatement
			   | s instanceof ForStatement
			   | s instanceof IfStatement
			   | s instanceof SwitchStatement
			   | s instanceof TryStatement
			   | s instanceof WhileStatement)
			{
				return true;
			}
			return false;
	}

	private boolean isOverridingToString(MethodDeclaration md)
	{
		if ( md.getName().toString().equals("toString") 
		   & md.getReturnType2() != null)
		{
			if (md.getReturnType2().toString().equals("String"))
			{
				if (md.parameters().size() == 0)
					return true;					
			}					
		}		
		return false;
	}

	private boolean isOverridingEquals(MethodDeclaration md) {

		if ( md.getName().toString().equals("equals") 
		   & md.getReturnType2() != null)
		{
			if (md.getReturnType2().toString().equals("boolean"))
			{
				List<Object> params = md.parameters();
				if (params.size() == 1 )
				{
					Object o = params.get(0);
					if (o instanceof SingleVariableDeclaration)
					{
						SingleVariableDeclaration sv = (SingleVariableDeclaration) o;
						if(sv.getType().toString().equals("Object"))
							return true;				
					}
				}
			}			
		}		
		return false;
	}

//	private boolean isStringOperand(ASTNode node) 
//	{		
//		List<Object> properties = node.structuralPropertiesForType();
//		for (Iterator<Object> iterator = properties.iterator(); iterator.hasNext();)
//		{
//			Object descriptor = iterator.next();
//			if (descriptor instanceof SimplePropertyDescriptor) 
//			{
//				SimplePropertyDescriptor simple = (SimplePropertyDescriptor) descriptor;
//				if (simple.getValueType() instanceof String)
//					return true;
//			}
//		}	
//		return false;
//	}
//
//	private boolean isObjectOperand(SimpleName node) 
//	{	
//		List<Object> properties = node.structuralPropertiesForType();
//		for (Iterator<Object> iterator = properties.iterator(); iterator.hasNext();)
//		{
//			Object descriptor = iterator.next();
//			if (descriptor instanceof SimplePropertyDescriptor) 
//			{
//				SimplePropertyDescriptor simple = (SimplePropertyDescriptor) descriptor;
//				Object value = node.getStructuralProperty(simple);
//
//				if ((simple.getValueType() instanceof Numb instanceofral) == false)
//					return true;
//			}
//		}	
//		return false;
//	}
//	
	private boolean isConceptNode(ASTNode node) {
		int type = node.getNodeType();
		if ( type == ASTNode.NUMBER_LITERAL 
		   | type == ASTNode.CHARACTER_LITERAL
		   | type == ASTNode.TYPE_LITERAL 
		   )
			return false;
		return true;
	}

	
	private void addConcept(String concept) {
		//TODO adding to concept is only for print purpose, later this var can be deleted
		if (concepts.contains(concept) == false) {
			concepts.add(concept.toString());
		}
		int startLineNumber = unit.getLineNumber(curNode.getStartPosition()) - 1;
		int nodeLength = curNode.getLength();
		int endLineNumber = unit.getLineNumber(curNode.getStartPosition()+ nodeLength) - 1;
		/*
		 * @see CompilationUnit.getLineNumber 
		 * this method return -2 if no line is associated with the node, -1 if position is not in the source
		 */
		
		if (startLineNumber < 0 & endLineNumber < 0)
			return; //insert no concept
		if (concept.equals("ClassDefinition"))
			endLineNumber = startLineNumber;
		List<AbstractTypeDeclaration> types = unit.types();
		String className = types.get(0).getName().getIdentifier();
		if (isTester)
			className = "Tester";
		if (db.isConnectedToWebex21() ) {
			int id = 0;
			if (isExample)
				id = db.getExampleID(question);
			else
				id = db.getQuestionID(question);	
			db.insertQuizConcept(id,question, concept,className+".java",startLineNumber,endLineNumber,isExample,sv);		
		}
	}
	
	private void addConcept(String concept, int startPosition, int length) {
		//TODO adding to concept is only for print purpose, later this var can be deleted
		if (concepts.contains(concept) == false) {
			concepts.add(concept.toString());
		}
		int startLineNumber = unit.getLineNumber(startPosition) - 1;
		int endLineNumber = unit.getLineNumber(startPosition + length) - 1;
		/*
		 * @see CompilationUnit.getLineNumber 
		 * this method return -2 if no line is associated with the node, -1 if position is not in the source
		 */
		if (startLineNumber < 0 & endLineNumber < 0)
			return; //insert no concept
		if (concept.equals("ClassDefinition"))
			endLineNumber = startLineNumber;
		List<AbstractTypeDeclaration> types = unit.types();
		String className = types.get(0).getName().getIdentifier();
		if (isTester)
			className = "Tester";
		if (db.isConnectedToWebex21() ) {
			int id = 0;
			if (isExample)
				id = db.getExampleID(question);
			else
				id = db.getQuestionID(question);
			db.insertQuizConcept(id,question, concept,className+".java",startLineNumber,endLineNumber,isExample,sv);		
		}
	}

	private boolean isAutoBoxingType(Type type) {
		for (String s : boxingTypes)
		{
			if (s.equals(type.toString()))
				return true;
		}
		return false;
	}
	
	private void checkAndAddStringVariableConcept(Type t){
		if (t.toString().equals("String"))
		{
			addConcept("StringVariable");
			addConcept("StringDataType");
		}
	}
	
    private void checkAndAddStringParametrizedType(ParameterizedType pt){
		List<Type> types = pt.typeArguments();
		for (Type t : types)
			if (t.toString().equals("String"))
				addConcept("StringDataType");		
	}
    
    private void checkAndAddObjectVariableConcept(Type t){
		if (t.toString().equals("Object"))
			addConcept("java.lang.Object");
	}
    
    public void clearParserData()
    {
    	unit = null;
    	db.disconnectFromWebex21();
    	db = null;
    	curNode = null;
    	clearLists();
    }

	public void parseExample(String title, String code,ServletContext sv,boolean isTester) {
		isExample = true;
		parse(title, code,sv,isTester);
	}
}
