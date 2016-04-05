-- MySQL dump 10.13  Distrib 5.6.24, for osx10.8 (x86_64)
--
-- Host: 127.0.0.1    Database: webex21jet
-- ------------------------------------------------------
-- Server version	5.6.25

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ae`
--

DROP TABLE IF EXISTS `ae`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ae` (
  `id` int(11) NOT NULL DEFAULT '0',
  `dissectionID` int(11) DEFAULT NULL,
  `title` varchar(45) NOT NULL,
  `concept` varchar(100) CHARACTER SET utf8 NOT NULL,
  `class` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `sline` int(11) DEFAULT '-1',
  `eline` int(11) DEFAULT '-1',
  `weight` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `direction` varchar(45) CHARACTER SET utf8 DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `assignexample`
--

DROP TABLE IF EXISTS `assignexample`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assignexample` (
  `AssignExampleID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `AssignUserID` int(10) unsigned NOT NULL DEFAULT '0',
  `DissectionID` int(10) unsigned NOT NULL DEFAULT '0',
  `taken` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`AssignExampleID`),
  KEY `Index_DissectionID` (`DissectionID`),
  KEY `Index_AssignUserID` (`AssignUserID`),
  CONSTRAINT `FK_assignexample_1` FOREIGN KEY (`AssignUserID`) REFERENCES `assignuser` (`AssignUserID`),
  CONSTRAINT `FK_assignexample_2` FOREIGN KEY (`DissectionID`) REFERENCES `ent_dissection` (`DissectionID`)
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `assignrate`
--

DROP TABLE IF EXISTS `assignrate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assignrate` (
  `AssignRateID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `AssignUserID` int(10) unsigned NOT NULL DEFAULT '0',
  `DissectionID` int(10) unsigned NOT NULL DEFAULT '0',
  `rating` float NOT NULL DEFAULT '0',
  PRIMARY KEY (`AssignRateID`),
  KEY `Index_DissectionID` (`DissectionID`),
  KEY `Index_AssignUserID` (`AssignUserID`),
  CONSTRAINT `FK_assignrate_1` FOREIGN KEY (`AssignUserID`) REFERENCES `assignuser` (`AssignUserID`),
  CONSTRAINT `FK_assignrate_2` FOREIGN KEY (`DissectionID`) REFERENCES `ent_dissection` (`DissectionID`)
) ENGINE=InnoDB AUTO_INCREMENT=158 DEFAULT CHARSET=utf8 COMMENT='InnoDB free: 68608 kB';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `assignuser`
--

DROP TABLE IF EXISTS `assignuser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assignuser` (
  `AssignUserID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`AssignUserID`),
  KEY `Index_AssignUserID` (`AssignUserID`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ent_class`
--

DROP TABLE IF EXISTS `ent_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ent_class` (
  `ClassID` int(10) unsigned NOT NULL DEFAULT '0',
  `ClassName` varchar(45) NOT NULL,
  `ClassTester` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ClassID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ent_concept`
--

DROP TABLE IF EXISTS `ent_concept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ent_concept` (
  `conID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uID` int(11) NOT NULL,
  `typeID` int(10) unsigned NOT NULL,
  `rdfID` varchar(45) NOT NULL,
  `title` varchar(63) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `ontID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`conID`,`ontID`),
  KEY `FK_ent_concept1_1` (`uID`),
  KEY `FK_ent_concept_2` (`ontID`),
  CONSTRAINT `FK_ent_concept1_1` FOREIGN KEY (`uID`) REFERENCES `ent_user` (`id`),
  CONSTRAINT `FK_ent_concept_2` FOREIGN KEY (`ontID`) REFERENCES `ent_ontology` (`ontID`)
) ENGINE=InnoDB AUTO_INCREMENT=236 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ent_dissection`
--

DROP TABLE IF EXISTS `ent_dissection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ent_dissection` (
  `DissectionID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `rdfID` varchar(45) NOT NULL DEFAULT '',
  `Name` varchar(255) NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `Author` varchar(45) NOT NULL DEFAULT '',
  `IsForJeliot` tinyint(1) NOT NULL DEFAULT '0',
  `domain` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`DissectionID`),
  UNIQUE KEY `Index_rdfID` (`rdfID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=924 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ent_domain`
--

DROP TABLE IF EXISTS `ent_domain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ent_domain` (
  `domain` varchar(45) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`domain`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ent_group`
--

DROP TABLE IF EXISTS `ent_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ent_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `ownerid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `Index_GroupID` (`id`) USING BTREE,
  KEY `Index_Name` (`name`) USING BTREE,
  KEY `Index_OwnerUserID` (`ownerid`) USING BTREE,
  CONSTRAINT `FK_ent_group_1` FOREIGN KEY (`ownerid`) REFERENCES `ent_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='InnoDB free: 10240 kB; (`groupowner`) REFER `quizpack/user`(';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ent_java_labels`
--

DROP TABLE IF EXISTS `ent_java_labels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ent_java_labels` (
  `label` varchar(100) NOT NULL,
  PRIMARY KEY (`label`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ent_jcontent_keyword`
--

DROP TABLE IF EXISTS `ent_jcontent_keyword`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ent_jcontent_keyword` (
  `contentName` varchar(45) NOT NULL,
  `keyword` varchar(45) NOT NULL,
  `tfidf` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ent_jexample_concept`
--

DROP TABLE IF EXISTS `ent_jexample_concept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ent_jexample_concept` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(45) CHARACTER SET latin1 NOT NULL,
  `concept` varchar(100) NOT NULL,
  `class` varchar(45) DEFAULT NULL,
  `sline` int(11) DEFAULT '-1',
  `eline` int(11) DEFAULT '-1',
  `weight` varchar(45) DEFAULT NULL,
  `direction` varchar(45) DEFAULT NULL,
  `DissectionID` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index` (`title`,`concept`)
) ENGINE=InnoDB AUTO_INCREMENT=93996 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ent_jquestion`
--

DROP TABLE IF EXISTS `ent_jquestion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ent_jquestion` (
  `QuestionID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `AuthorID` int(11) NOT NULL,
  `GroupID` int(11) NOT NULL,
  `Title` varchar(45) NOT NULL,
  `Description` varchar(255) NOT NULL,
  `Privacy` int(10) unsigned NOT NULL,
  `domain` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`QuestionID`),
  KEY `FK_ent_jquestion_1` (`AuthorID`),
  KEY `FK_ent_jquestion_2` (`GroupID`),
  CONSTRAINT `FK_ent_jquestion_1` FOREIGN KEY (`AuthorID`) REFERENCES `ent_user` (`id`),
  CONSTRAINT `FK_ent_jquestion_2` FOREIGN KEY (`GroupID`) REFERENCES `ent_group` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ent_jquiz`
--

DROP TABLE IF EXISTS `ent_jquiz`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ent_jquiz` (
  `QuizID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `AuthorID` int(11) NOT NULL,
  `GroupID` int(11) NOT NULL,
  `Title` varchar(45) NOT NULL,
  `Description` varchar(255) NOT NULL,
  `Code` mediumblob NOT NULL,
  `MinVar` int(11) NOT NULL,
  `MaxVar` int(11) NOT NULL,
  `AnsType` int(10) unsigned NOT NULL,
  `Privacy` int(10) unsigned NOT NULL,
  `rdfID` varchar(45) NOT NULL,
  `QuesType` int(10) unsigned NOT NULL,
  PRIMARY KEY (`QuizID`),
  KEY `FK_ent_jquiz_1` (`GroupID`),
  KEY `FK_ent_jquiz_2` (`AuthorID`),
  CONSTRAINT `FK_ent_jquiz_1` FOREIGN KEY (`GroupID`) REFERENCES `ent_group` (`id`),
  CONSTRAINT `FK_ent_jquiz_2` FOREIGN KEY (`AuthorID`) REFERENCES `ent_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=372 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ent_jquiz_concept`
--

DROP TABLE IF EXISTS `ent_jquiz_concept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ent_jquiz_concept` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(45) CHARACTER SET latin1 NOT NULL,
  `concept` varchar(100) NOT NULL,
  `class` varchar(45) DEFAULT NULL,
  `sline` int(11) DEFAULT '-1',
  `eline` int(11) DEFAULT '-1',
  `weight` varchar(45) DEFAULT NULL,
  `direction` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index` (`title`,`concept`)
) ENGINE=InnoDB AUTO_INCREMENT=166992 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ent_learning_goal`
--

DROP TABLE IF EXISTS `ent_learning_goal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ent_learning_goal` (
  `GoalID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) NOT NULL DEFAULT '',
  `ScopeID` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`GoalID`,`ScopeID`),
  KEY `Index_ScopeID` (`ScopeID`),
  CONSTRAINT `FK_learning_goal_1` FOREIGN KEY (`ScopeID`) REFERENCES `ent_scope` (`ScopeID`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ent_line`
--

DROP TABLE IF EXISTS `ent_line`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ent_line` (
  `LineID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `DissectionID` int(10) unsigned NOT NULL DEFAULT '0',
  `LineIndex` smallint(6) unsigned NOT NULL DEFAULT '0',
  `Code` text NOT NULL,
  `Comment` text,
  `Rating` float NOT NULL DEFAULT '0',
  `EvaluateHit` int(10) unsigned NOT NULL DEFAULT '0',
  `flag` char(1) NOT NULL DEFAULT '' COMMENT '0:null; 1:filled',
  PRIMARY KEY (`LineID`),
  KEY `Index_Dissection` (`DissectionID`),
  CONSTRAINT `FK_ent_line_1` FOREIGN KEY (`DissectionID`) REFERENCES `ent_dissection` (`DissectionID`)
) ENGINE=InnoDB AUTO_INCREMENT=22186 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ent_line_a`
--

DROP TABLE IF EXISTS `ent_line_a`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ent_line_a` (
  `LineID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `DissectionID` int(10) unsigned NOT NULL DEFAULT '0',
  `LineIndex` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `AssignUserID` int(10) unsigned NOT NULL DEFAULT '0',
  `Annotation` varchar(255) CHARACTER SET latin1 NOT NULL DEFAULT '0',
  PRIMARY KEY (`LineID`),
  KEY `Index_AssignUserID` (`AssignUserID`),
  KEY `Index_DissectionID` (`DissectionID`),
  CONSTRAINT `FK_ent_line_a_1` FOREIGN KEY (`DissectionID`) REFERENCES `ent_dissection` (`DissectionID`),
  CONSTRAINT `FK_ent_line_a_2` FOREIGN KEY (`AssignUserID`) REFERENCES `assignuser` (`AssignUserID`)
) ENGINE=InnoDB AUTO_INCREMENT=3302 DEFAULT CHARSET=utf8 COMMENT='InnoDB free: 68608 kB';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ent_ontology`
--

DROP TABLE IF EXISTS `ent_ontology`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ent_ontology` (
  `ontID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) NOT NULL,
  `Description` varchar(255) NOT NULL,
  `rdfID` varchar(45) NOT NULL,
  `Privacy` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '0:private 1:public',
  PRIMARY KEY (`ontID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ent_scope`
--

DROP TABLE IF EXISTS `ent_scope`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ent_scope` (
  `ScopeID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `rdfID` varchar(15) NOT NULL DEFAULT '',
  `Name` varchar(45) NOT NULL DEFAULT '',
  `Description` varchar(255) DEFAULT NULL,
  `domain` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ScopeID`),
  UNIQUE KEY `Index_rdfID` (`rdfID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ent_user`
--

DROP TABLE IF EXISTS `ent_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ent_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` char(25) DEFAULT NULL,
  `password` char(255) NOT NULL,
  `role` char(15) NOT NULL,
  `login` char(15) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Index_login` (`login`),
  KEY `Index_UserID` (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ent_user_pr`
--

DROP TABLE IF EXISTS `ent_user_pr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ent_user_pr` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `rand` char(255) NOT NULL DEFAULT '',
  `time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY `rell` (`user_id`),
  CONSTRAINT `rell` FOREIGN KEY (`user_id`) REFERENCES `ent_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rel_adjusted_concept_direction`
--

DROP TABLE IF EXISTS `rel_adjusted_concept_direction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rel_adjusted_concept_direction` (
  `int` int(11) NOT NULL AUTO_INCREMENT COMMENT '- weight is the tf-idf value. All contents in corpus are considered for calculating this value.',
  `content_name` varchar(45) DEFAULT NULL,
  `topic` varchar(45) DEFAULT NULL,
  `concept` varchar(45) DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `direction` varchar(45) DEFAULT NULL,
  `content_type` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`int`)
) ENGINE=MyISAM AUTO_INCREMENT=8471 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rel_content_concept`
--

DROP TABLE IF EXISTS `rel_content_concept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rel_content_concept` (
  `id` int(11) NOT NULL DEFAULT '0',
  `title` varchar(45) CHARACTER SET latin1 NOT NULL,
  `concept` varchar(100) NOT NULL,
  `class` varchar(45) DEFAULT NULL,
  `sline` int(11) DEFAULT '-1',
  `eline` int(11) DEFAULT '-1',
  `weight` varchar(45) DEFAULT NULL,
  `direction` varchar(45) DEFAULT NULL,
  `content_type` varchar(45) DEFAULT NULL,
  KEY `index1` (`title`,`concept`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rel_dissection_concept`
--

DROP TABLE IF EXISTS `rel_dissection_concept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rel_dissection_concept` (
  `DissectionID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ontID` int(10) unsigned NOT NULL,
  `conID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`DissectionID`,`ontID`,`conID`),
  KEY `FK_rel_dissection_concept_2` (`ontID`),
  KEY `FK_rel_dissection_concept_3` (`conID`),
  CONSTRAINT `FK_rel_dissection_concept_1` FOREIGN KEY (`DissectionID`) REFERENCES `ent_dissection` (`DissectionID`),
  CONSTRAINT `FK_rel_dissection_concept_2` FOREIGN KEY (`ontID`) REFERENCES `ent_ontology` (`ontID`),
  CONSTRAINT `FK_rel_dissection_concept_3` FOREIGN KEY (`conID`) REFERENCES `ent_concept` (`conID`)
) ENGINE=InnoDB AUTO_INCREMENT=199 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rel_dissection_privacy`
--

DROP TABLE IF EXISTS `rel_dissection_privacy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rel_dissection_privacy` (
  `DissectionID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Uid` int(11) NOT NULL,
  `privacy` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '0:private 1:public',
  PRIMARY KEY (`DissectionID`,`Uid`),
  KEY `FK_rel_dissection_privacy_2` (`Uid`),
  CONSTRAINT `FK_rel_dissection_privacy_1` FOREIGN KEY (`DissectionID`) REFERENCES `ent_dissection` (`DissectionID`),
  CONSTRAINT `FK_rel_dissection_privacy_2` FOREIGN KEY (`Uid`) REFERENCES `ent_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=924 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rel_group_user`
--

DROP TABLE IF EXISTS `rel_group_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rel_group_user` (
  `UserID` int(11) NOT NULL DEFAULT '0',
  `GroupID` int(11) NOT NULL DEFAULT '0',
  `Rights` tinyint(3) unsigned DEFAULT NULL,
  PRIMARY KEY (`GroupID`,`UserID`),
  KEY `Index_UserIDGroupID` (`UserID`,`GroupID`),
  KEY `Index_UserID` (`UserID`),
  KEY `Index_GroupID` (`GroupID`),
  CONSTRAINT `FK_rel_group_user_1` FOREIGN KEY (`UserID`) REFERENCES `ent_user` (`id`),
  CONSTRAINT `FK_rel_group_user_2` FOREIGN KEY (`GroupID`) REFERENCES `ent_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='InnoDB free: 10240 kB; (`userid`) REFER `quizpack/user`(`id`';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rel_java_ont_class_subclass`
--

DROP TABLE IF EXISTS `rel_java_ont_class_subclass`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rel_java_ont_class_subclass` (
  `class` varchar(45) NOT NULL,
  `subclass` text NOT NULL,
  PRIMARY KEY (`class`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rel_jcontent_textbasedsim`
--

DROP TABLE IF EXISTS `rel_jcontent_textbasedsim`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rel_jcontent_textbasedsim` (
  `questionName` varchar(45) NOT NULL,
  `exampleName` varchar(45) NOT NULL,
  `similarity` double NOT NULL COMMENT 'cosine similarity based on tfidf; provided by Yun on 09/04/13'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rel_question_quiz`
--

DROP TABLE IF EXISTS `rel_question_quiz`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rel_question_quiz` (
  `QuestionID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `QuizID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`QuestionID`,`QuizID`),
  KEY `FK_rel_question_quiz_2` (`QuizID`),
  CONSTRAINT `FK_rel_question_quiz_1` FOREIGN KEY (`QuestionID`) REFERENCES `ent_jquestion` (`QuestionID`),
  CONSTRAINT `FK_rel_question_quiz_2` FOREIGN KEY (`QuizID`) REFERENCES `ent_jquiz` (`QuizID`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rel_quiz_class`
--

DROP TABLE IF EXISTS `rel_quiz_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rel_quiz_class` (
  `QuizID` int(10) unsigned NOT NULL,
  `ClassID` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`QuizID`,`ClassID`),
  KEY `FK_rel_quiz_class_2` (`ClassID`),
  CONSTRAINT `FK_rel_quiz_class_1` FOREIGN KEY (`QuizID`) REFERENCES `ent_jquiz` (`QuizID`),
  CONSTRAINT `FK_rel_quiz_class_2` FOREIGN KEY (`ClassID`) REFERENCES `ent_class` (`ClassID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rel_scope_dissection`
--

DROP TABLE IF EXISTS `rel_scope_dissection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rel_scope_dissection` (
  `ScopeID` int(10) unsigned NOT NULL DEFAULT '0',
  `DissectionID` int(10) unsigned NOT NULL DEFAULT '0',
  `GoalID` int(10) unsigned NOT NULL DEFAULT '0',
  `diff` int(10) unsigned NOT NULL DEFAULT '0',
  `comp` int(10) unsigned NOT NULL DEFAULT '0',
  `create_dt` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `lastmodify_dt` datetime DEFAULT NULL,
  `taken` int(10) unsigned NOT NULL DEFAULT '0',
  `Rating` float NOT NULL DEFAULT '0',
  `EvaluateHit` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`DissectionID`,`ScopeID`),
  KEY `Index_ScopeID` (`ScopeID`),
  KEY `Index_DissectionID` (`DissectionID`),
  KEY `Index_GoalID` (`GoalID`),
  CONSTRAINT `FK_rel_scope_dissection_1` FOREIGN KEY (`DissectionID`) REFERENCES `ent_dissection` (`DissectionID`),
  CONSTRAINT `FK_rel_scope_dissection_2` FOREIGN KEY (`ScopeID`) REFERENCES `ent_scope` (`ScopeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rel_scope_privacy`
--

DROP TABLE IF EXISTS `rel_scope_privacy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rel_scope_privacy` (
  `ScopeID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Uid` int(11) NOT NULL,
  `Privacy` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`ScopeID`,`Uid`),
  KEY `FK_rel_scope_privacy_2` (`Uid`),
  CONSTRAINT `FK_rel_scope_privacy_1` FOREIGN KEY (`ScopeID`) REFERENCES `ent_scope` (`ScopeID`),
  CONSTRAINT `FK_rel_scope_privacy_2` FOREIGN KEY (`Uid`) REFERENCES `ent_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rel_topic_dissection`
--

DROP TABLE IF EXISTS `rel_topic_dissection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rel_topic_dissection` (
  `topicID` int(11) NOT NULL,
  `dissectionID` int(11) NOT NULL,
  PRIMARY KEY (`topicID`,`dissectionID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `temp`
--

DROP TABLE IF EXISTS `temp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `temp` (
  `title` varchar(45) CHARACTER SET latin1 NOT NULL,
  `concept` varchar(100) NOT NULL,
  `tfidf` double DEFAULT NULL,
  `direction` varchar(45) DEFAULT NULL,
  `content_type` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `temp121913_ent_jcontent_concept_tf`
--

DROP TABLE IF EXISTS `temp121913_ent_jcontent_concept_tf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `temp121913_ent_jcontent_concept_tf` (
  `id` int(11) NOT NULL DEFAULT '0',
  `quizID` int(11) DEFAULT NULL,
  `title` varchar(45) CHARACTER SET latin1 NOT NULL,
  `concept` varchar(100) NOT NULL,
  `class` varchar(45) DEFAULT NULL,
  `sline` int(11) DEFAULT '-1',
  `eline` int(11) DEFAULT '-1',
  `weight` varchar(45) DEFAULT NULL,
  `direction` varchar(45) DEFAULT NULL,
  `tf` bigint(21) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `temp2_ent_jcontent_concept`
--

DROP TABLE IF EXISTS `temp2_ent_jcontent_concept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `temp2_ent_jcontent_concept` (
  `id` int(11) NOT NULL DEFAULT '0',
  `title` varchar(45) CHARACTER SET latin1 NOT NULL,
  `concept` varchar(100) NOT NULL,
  `class` varchar(45) DEFAULT NULL,
  `sline` int(11) DEFAULT '-1',
  `eline` int(11) DEFAULT '-1',
  `weight` varchar(45) DEFAULT NULL,
  `direction` varchar(45) DEFAULT NULL,
  `content_type` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `temp2_ent_jcontent_concept_df_idf`
--

DROP TABLE IF EXISTS `temp2_ent_jcontent_concept_df_idf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `temp2_ent_jcontent_concept_df_idf` (
  `concept` varchar(100) NOT NULL,
  `df` bigint(21) NOT NULL DEFAULT '0',
  `idf` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `temp2_ent_jcontent_concept_tf`
--

DROP TABLE IF EXISTS `temp2_ent_jcontent_concept_tf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `temp2_ent_jcontent_concept_tf` (
  `title` varchar(45) CHARACTER SET latin1 NOT NULL,
  `concept` varchar(100) NOT NULL,
  `tf` bigint(21) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `temp2_ent_jcontent_tfidf`
--

DROP TABLE IF EXISTS `temp2_ent_jcontent_tfidf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `temp2_ent_jcontent_tfidf` (
  `title` varchar(45) CHARACTER SET latin1 NOT NULL,
  `concept` varchar(100) NOT NULL,
  `tf` bigint(21) NOT NULL DEFAULT '0',
  `log(tf+1)` double DEFAULT NULL,
  `df` bigint(21) NOT NULL DEFAULT '0',
  `idf` double DEFAULT NULL,
  `tfidf` double DEFAULT NULL,
  `log(tf+1)idf` double DEFAULT NULL,
  KEY `index1` (`concept`,`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `temp_ent_jcontent_tfidf`
--

DROP TABLE IF EXISTS `temp_ent_jcontent_tfidf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `temp_ent_jcontent_tfidf` (
  `title` varchar(45) CHARACTER SET latin1 NOT NULL DEFAULT '',
  `concept` varchar(100) NOT NULL DEFAULT '',
  `tf` bigint(21) NOT NULL DEFAULT '0',
  `log(tf+1)` double DEFAULT NULL,
  `df` bigint(21) NOT NULL DEFAULT '0',
  `idf` double DEFAULT NULL,
  `tfidf` double DEFAULT NULL,
  `log(tf+1)idf` double DEFAULT NULL,
  PRIMARY KEY (`title`,`concept`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `temp_ent_jquiz_concept_df_idf`
--

DROP TABLE IF EXISTS `temp_ent_jquiz_concept_df_idf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `temp_ent_jquiz_concept_df_idf` (
  `concept` varchar(100) NOT NULL,
  `df` bigint(21) NOT NULL DEFAULT '0',
  `idf` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `temp_ent_jquiz_concept_tf`
--

DROP TABLE IF EXISTS `temp_ent_jquiz_concept_tf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `temp_ent_jquiz_concept_tf` (
  `title` varchar(45) CHARACTER SET latin1 NOT NULL,
  `concept` varchar(100) NOT NULL,
  `count(concept)` bigint(21) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `temp_new7`
--

DROP TABLE IF EXISTS `temp_new7`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `temp_new7` (
  `DissectionID` int(10) unsigned NOT NULL DEFAULT '0',
  `rdfID` varchar(45) NOT NULL DEFAULT '',
  `AllCode` longtext
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `topiccontent`
--

DROP TABLE IF EXISTS `topiccontent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `topiccontent` (
  `order` smallint(8) NOT NULL DEFAULT '0',
  `topic_name` varchar(100) NOT NULL,
  `rdfid` varchar(45) NOT NULL,
  `display_name` varchar(100) NOT NULL,
  `display_order` smallint(4) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-03-29 12:05:55
