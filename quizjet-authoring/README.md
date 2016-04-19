# quizjet
How to run this project?

(1)Import the database using the sql file "webex21.sql" in the "db file" folder(If the database doesn't exist.).

(2)Import the whole project into Eclipse. 

(3) Configure the database name string "DBNAME" in edu.pitt.pawslab.quizpet.instance.Setting. For example,
"webex21".

(4)Configure db information in the /WEB-INF/web.xml. Configure the url of db.webexURL AND db.user and db.passwd.

(5)Save all the configuration. Run it locally in eclipse to test the project.

(6)Export it as an War file and deploy it on tomcat.




How to develop the project?

(1) The UI is implemented using React framework.
The files are in React Component.

Here are some useful commands using React.

sudo npm install webpack-dev-server -g

npm install --save-dev jsx-loader

npm install --save react react-dom

npm install brace

sudo npm install react-ace

webpack-dev-server --host 0.0.0.0 --port 8090

(2)
Before you start, try to have a basic idea of React and Webpack. When you develop locally, you can edit the react project directly. The webpack host the react project and will automatically generate a bundle js file on the path /assets/bundle.js in the react project. When you want to deploy the project, just copy the compressed js into the resources/js/bundle.min.js in the Eclipse project.
Check the following lines in home.jsp to understand how js file is imported.

<c:choose>

		<c:when test="${domain == 'localhost'}">

	<script src="http://localhost:9090/webpack-dev-server.js"></script>

    <script type="text/javascript" src="http://localhost:9090/assets/bundle.js"></script>

    	</c:when>

    	<c:otherwise>

    <script type="text/javascript" src="${baseURL}/resources/js/bundle.min.js"></script>

    	</c:otherwise>

	</c:choose>
	




	
