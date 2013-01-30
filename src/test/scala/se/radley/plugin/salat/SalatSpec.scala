package se.radley.plugin.salat

import org.specs2.mutable.Specification
import play.api._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import java.io.File
import play.api.Play.current
import com.mongodb.casbah._
import com.mongodb.ServerAddress

object SalatSpec extends Specification {

  lazy val salatApp = FakeApplication(
    additionalPlugins = Seq("se.radley.plugin.salat.SalatPlugin")
  )

  "Salat Plugin with basic config" should {

    lazy val app = salatApp.copy(
      additionalConfiguration = Map(
        ("mongodb.default.db" -> "salat-test"),
        ("mongodb.default.writeconcern" -> "normal")
      )
    )

    lazy val salat = app.plugin[SalatPlugin].get

    running(app) {
      "start" in {
        salat must beAnInstanceOf[SalatPlugin]
      }

      "return a MongoCollection" in {
        val col = salat.collection("salat-collection")
        col must beAnInstanceOf[MongoCollection]
      }

      "set write concern" in {
        val source = salat.source("default")
        source.writeConcern must equalTo(WriteConcern.Normal)
      }

      "fail if source doesn't exist" in {
        salat.collection("salat-collection", "sourcethatdoesntexist") must throwAn[PlayException]
      }
    }

    "be disabled if no configuration exists" in {
      val app = FakeApplication(additionalPlugins = Seq("se.radley.plugin.salat.SalatPlugin"))
      running(app) {
        app.plugin[SalatPlugin] must beNone
      }
    }
  }

  "Salat Plugin with basic config and No Options set" should {

    lazy val app = salatApp.copy(
      additionalConfiguration = Map(
        ("mongodb.default.db" -> "salat-test"),
        ("mongodb.default.writeconcern" -> "normal")
      )
    )

    lazy val salat = app.plugin[SalatPlugin].get

    running(app) {
      "start" in {
        salat must beAnInstanceOf[SalatPlugin]
      }

      "return a MongoCollection" in {
        val col = salat.collection("salat-collection")
        col must beAnInstanceOf[MongoCollection]
      }

      "set write concern" in {
        val source = salat.source("default")
        source.writeConcern must equalTo(WriteConcern.Normal)
      }

      "default autoConnectRetry" in {
        val source = salat.source("default")
        source.autoConnectRetry must equalTo(false)
      }

      "default socketKeepAlive" in {
        val source = salat.source("default")
        source.socketKeepAlive must equalTo(false)
      }

      "default socketTimeout" in {
        val source = salat.source("default")
        source.socketTimeout must equalTo(0)
      }

      "default connectTimeout" in {
        val source = salat.source("default")
        source.connectTimeout must equalTo(10000)
      }

      "default connectionsPerHost" in {
        val source = salat.source("default")
        source.connectionsPerHost must equalTo(10)
      }

      "default threadsAllowedToBlockForConnectionMultiplier" in {
        val source = salat.source("default")
        source.threadsAllowedToBlockForConnectionMultiplier must equalTo(5)
      }

      "fail if source doesn't exist" in {
        salat.collection("salat-collection", "sourcethatdoesntexist") must throwAn[PlayException]
      }
    }

    "be disabled if no configuration exists" in {
      val app = FakeApplication(additionalPlugins = Seq("se.radley.plugin.salat.SalatPlugin"))
      running(app) {
        app.plugin[SalatPlugin] must beNone
      }
    }
  }


  "Salat Plugin with basic config and options all set" should {

    lazy val app = salatApp.copy(
      additionalConfiguration = Map(
        ("mongodb.default.db" -> "salat-test"),
        ("mongodb.default.writeconcern" -> "normal"),
        ("mongodb.default.autoConnectRetry" -> true),
        ("mongodb.default.socketKeepAlive" -> true),
        ("mongodb.default.socketTimeout" -> 100),
        ("mongodb.default.connectTimeout" -> 200),
        ("mongodb.default.connectionsPerHost" -> 300),
        ("mongodb.default.threadsAllowedToBlockForConnectionMultiplier" -> 400)
      )
    )

    lazy val salat = app.plugin[SalatPlugin].get

    running(app) {
      "start" in {
        salat must beAnInstanceOf[SalatPlugin]
      }

      "return a MongoCollection" in {
        val col = salat.collection("salat-collection")
        col must beAnInstanceOf[MongoCollection]
      }

      "set write concern" in {
        val source = salat.source("default")
        source.writeConcern must equalTo(WriteConcern.Normal)
      }

      "set autoConnectRetry" in {
        val source = salat.source("default")
        source.autoConnectRetry must equalTo(true)
      }

      "set socketKeepAlive" in {
        val source = salat.source("default")
        source.socketKeepAlive must equalTo(true)
      }

      "set socketTimeout" in {
        val source = salat.source("default")
        source.socketTimeout must equalTo(100)
      }

      "set connectTimeout" in {
        val source = salat.source("default")
        source.connectTimeout must equalTo(200)
      }

      "set connectionsPerHost" in {
        val source = salat.source("default")
        source.connectionsPerHost must equalTo(300)
      }

      "set threadsAllowedToBlockForConnectionMultiplier" in {
        val source = salat.source("default")
        source.threadsAllowedToBlockForConnectionMultiplier must equalTo(400)
      }

      "fail if source doesn't exist" in {
        salat.collection("salat-collection", "sourcethatdoesntexist") must throwAn[PlayException]
      }
    }

    "be disabled if no configuration exists" in {
      val app = FakeApplication(additionalPlugins = Seq("se.radley.plugin.salat.SalatPlugin"))
      running(app) {
        app.plugin[SalatPlugin] must beNone
      }
    }
  }

  "Salat Plugin with uri config" should {

    lazy val app = salatApp.copy(
      additionalConfiguration = Map(
        ("mongodb.default.uri" -> "mongodb://127.0.0.1:27017/salat-test")
      )
    )

    lazy val salat = app.plugin[SalatPlugin].get

    running(app) {
      "start" in {
        salat must beAnInstanceOf[SalatPlugin]
      }

      "return a MongoCollection" in {
        val col = salat.collection("salat-collection")
        col must beAnInstanceOf[MongoCollection]
      }

      "populate hosts from URI" in {
        salat must beAnInstanceOf[SalatPlugin]
        val source = salat.source("default")
        source.hosts must equalTo(List(new ServerAddress("127.0.0.1", 27017)))
      }

      /*
      // @todo if we need to test username and password we need to use an embedded mongo instance
      "populate username and password from URI" in {
        salat must beAnInstanceOf[SalatPlugin]
        val source = salat.source("default")
        source.user must equalTo(Some("leon"))
        source.password must equalTo(Some("password"))
      }*/
    }
  }

  "Salat Plugin with uri config and no options" should {

    lazy val app = salatApp.copy(
      additionalConfiguration = Map(
        ("mongodb.default.uri" -> "mongodb://127.0.0.1:27017/salat-test")
      )
    )

    lazy val salat = app.plugin[SalatPlugin].get

    running(app) {
      "start" in {
        salat must beAnInstanceOf[SalatPlugin]
      }

      "return a MongoCollection" in {
        val col = salat.collection("salat-collection")
        col must beAnInstanceOf[MongoCollection]
      }

      "populate hosts from URI" in {
        salat must beAnInstanceOf[SalatPlugin]
        val source = salat.source("default")
        source.hosts must equalTo(List(new ServerAddress("127.0.0.1", 27017)))
      }

      "default autoConnectRetry" in {
        val source = salat.source("default")
        source.autoConnectRetry must equalTo(false)
      }

      "default socketKeepAlive" in {
        val source = salat.source("default")
        source.socketKeepAlive must equalTo(false)
      }

      "default socketTimeout" in {
        val source = salat.source("default")
        source.socketTimeout must equalTo(0)
      }

      "default connectTimeout" in {
        val source = salat.source("default")
        source.connectTimeout must equalTo(10000)
      }

      "default connectionsPerHost" in {
        val source = salat.source("default")
        source.connectionsPerHost must equalTo(10)
      }

      "default threadsAllowedToBlockForConnectionMultiplier" in {
        val source = salat.source("default")
        source.threadsAllowedToBlockForConnectionMultiplier must equalTo(5)
      }
    }
  }

  "Salat Plugin with uri config and all options" should {

    lazy val app = salatApp.copy(
      additionalConfiguration = Map(
        ("mongodb.default.uri" -> "mongodb://127.0.0.1:27017/salat-test?autoConnectRetry=true;socketTimeoutMS=100;connectTimeOutMS=200")
      )
    )

    lazy val salat = app.plugin[SalatPlugin].get

    running(app) {
      "start" in {
        salat must beAnInstanceOf[SalatPlugin]
      }

      "return a MongoCollection" in {
        val col = salat.collection("salat-collection")
        col must beAnInstanceOf[MongoCollection]
      }

      "populate hosts from URI" in {
        salat must beAnInstanceOf[SalatPlugin]
        val source = salat.source("default")
        source.hosts must equalTo(List(new ServerAddress("127.0.0.1", 27017)))
      }
      "set autoConnectRetry" in {
        val source = salat.source("default")
        source.autoConnectRetry must equalTo(true)
      }

      "set socketTimeout" in {
        val source = salat.source("default")
        source.socketTimeout must equalTo(100)
      }

      "set connectTimeout" in {
        val source = salat.source("default")
        source.connectTimeout must equalTo(200)
      }
    }
  }


  "Salat Plugin with multiple uri config" should {
    lazy val app = salatApp.copy(
      additionalConfiguration = Map(
        ("mongodb.default.uri" -> "mongodb://127.0.0.1:27017,mongodb.org:1337/salat-test")
      )
    )

    lazy val salat = app.plugin[SalatPlugin].get

    running(app) {
      "start" in {
        salat must beAnInstanceOf[SalatPlugin]
      }

      "return a MongoCollection" in {
        val col = salat.collection("salat-collection")
        col must beAnInstanceOf[MongoCollection]
      }

      "populate hosts with multiple URIs" in {
        val source = salat.source("default")
        source.hosts must equalTo(List(new ServerAddress("127.0.0.1", 27017), new ServerAddress("mongodb.org", 1337)))
      }
    }
  }

  "Salat Plugin with replicaset config" should {

    lazy val app = FakeApplication(
      additionalPlugins = Seq("se.radley.plugin.salat.SalatPlugin"),
      additionalConfiguration = Map(
        ("mongodb.default.db" -> "salat-test"),
        ("mongodb.default.replicaset.host1.host" -> "10.0.0.1"),
        ("mongodb.default.replicaset.host2.host" -> "10.0.0.2"),
        ("mongodb.default.replicaset.host2.port" -> "27018")
      )
    )

    lazy val salat = app.plugin[SalatPlugin].get

    running(app) {
      "start" in {
        salat must beAnInstanceOf[SalatPlugin]
      }

      "populate hosts from config" in {
        val source = salat.source("default")
        source.hosts must equalTo(List(new ServerAddress("10.0.0.1", 27017), new ServerAddress("10.0.0.2", 27018)))
      }
    }
  }

  "Salat Plugin with replicaset config and no options" should {

    lazy val app = FakeApplication(
      additionalPlugins = Seq("se.radley.plugin.salat.SalatPlugin"),
      additionalConfiguration = Map(
        ("mongodb.default.db" -> "salat-test"),
        ("mongodb.default.replicaset.host1.host" -> "10.0.0.1"),
        ("mongodb.default.replicaset.host2.host" -> "10.0.0.2"),
        ("mongodb.default.replicaset.host2.port" -> "27018")
      )
    )

    lazy val salat = app.plugin[SalatPlugin].get

    running(app) {
      "start" in {
        salat must beAnInstanceOf[SalatPlugin]
      }

      "populate hosts from config" in {
        val source = salat.source("default")
        source.hosts must equalTo(List(new ServerAddress("10.0.0.1", 27017), new ServerAddress("10.0.0.2", 27018)))
      }
      "default autoConnectRetry" in {
        val source = salat.source("default")
        source.autoConnectRetry must equalTo(false)
      }

      "default socketKeepAlive" in {
        val source = salat.source("default")
        source.socketKeepAlive must equalTo(false)
      }

      "default socketTimeout" in {
        val source = salat.source("default")
        source.socketTimeout must equalTo(0)
      }

      "default connectTimeout" in {
        val source = salat.source("default")
        source.connectTimeout must equalTo(10000)
      }

      "default connectionsPerHost" in {
        val source = salat.source("default")
        source.connectionsPerHost must equalTo(10)
      }

      "default threadsAllowedToBlockForConnectionMultiplier" in {
        val source = salat.source("default")
        source.threadsAllowedToBlockForConnectionMultiplier must equalTo(5)
      }
    }
  }

  "Salat Plugin with replicaset config and all options" should {

    lazy val app = FakeApplication(
      additionalPlugins = Seq("se.radley.plugin.salat.SalatPlugin"),
      additionalConfiguration = Map(
        ("mongodb.default.db" -> "salat-test"),
        ("mongodb.default.replicaset.host1.host" -> "10.0.0.1"),
        ("mongodb.default.replicaset.host2.host" -> "10.0.0.2"),
        ("mongodb.default.replicaset.host2.port" -> "27018"),
        ("mongodb.default.autoConnectRetry" -> true),
        ("mongodb.default.socketKeepAlive" -> true),
        ("mongodb.default.socketTimeout" -> 100),
        ("mongodb.default.connectTimeout" -> 200),
        ("mongodb.default.connectionsPerHost" -> 300),
        ("mongodb.default.threadsAllowedToBlockForConnectionMultiplier" -> 400)
      )
    )

    lazy val salat = app.plugin[SalatPlugin].get

    running(app) {
      "start" in {
        salat must beAnInstanceOf[SalatPlugin]
      }

      "populate hosts from config" in {
        val source = salat.source("default")
        source.hosts must equalTo(List(new ServerAddress("10.0.0.1", 27017), new ServerAddress("10.0.0.2", 27018)))
      }

      "set autoConnectRetry" in {
        val source = salat.source("default")
        source.autoConnectRetry must equalTo(true)
      }

      "set socketKeepAlive" in {
        val source = salat.source("default")
        source.socketKeepAlive must equalTo(true)
      }

      "set socketTimeout" in {
        val source = salat.source("default")
        source.socketTimeout must equalTo(100)
      }

      "set connectTimeout" in {
        val source = salat.source("default")
        source.connectTimeout must equalTo(200)
      }

      "set connectionsPerHost" in {
        val source = salat.source("default")
        source.connectionsPerHost must equalTo(300)
      }

      "set threadsAllowedToBlockForConnectionMultiplier" in {
        val source = salat.source("default")
        source.threadsAllowedToBlockForConnectionMultiplier must equalTo(400)
      }

      "fail if source doesn't exist" in {
        salat.collection("salat-collection", "sourcethatdoesntexist") must throwAn[PlayException]
      }
    }

    "be disabled if no configuration exists" in {
      val app = FakeApplication(additionalPlugins = Seq("se.radley.plugin.salat.SalatPlugin"))
      running(app) {
        app.plugin[SalatPlugin] must beNone
      }
    }
  }



}
