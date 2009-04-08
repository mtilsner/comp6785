dataSource {
	pooled = true
	driverClassName = "com.mysql.jdbc.Driver"
}
hibernate {
    cache.use_second_level_cache=true
    cache.use_query_cache=true
    cache.provider_class='com.opensymphony.oscache.hibernate.OSCacheProvider'
}
// environment specific settings
environments {
	development {
		dataSource {
			dbCreate = "update"
			url = "jdbc:mysql://www.tilsner.eu:3306/cubansea_development?autoReconnect=true"
			username = "cubansea-admin"
			password = "infoViz"
		}
	}
	test {
		dataSource {
			dbCreate = "update"
			url = "jdbc:mysql://www.tilsner.eu:3306/cubansea_development?autoReconnect=true"
			username = "cubansea-admin"
			password = "infoViz"
		}
	}
	production {
		dataSource {
			dbCreate = "update"
			url = "jdbc:mysql://www.tilsner.eu:3306/cubansea_development?autoReconnect=true"
			username = "cubansea-admin"
			password = "infoViz"
		}
	}
}
