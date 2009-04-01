class UrlMappings {
    static mappings = {
      "/$action?"{
    	  controller = "search"
      }
      "/$controller/$action?/$id?"{
	      constraints {
			 // apply constraints here
		  }
	  }
//      "/"(view:"/index")
	  "500"(view:'/error')
	}
}
