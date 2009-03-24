class SearchController {

	SearchService searchService
	
    def index = {
    	if(params.q) {
    		session["terms"]	= searchService.terms(params.q)
    		session["clusters"] = searchService.search(session["terms"])
    	}
    	if(session["terms"] && session["clusters"])
    		render(view: "search", model: [q: params?.q,terms:session["terms"],
    		                               clusters:session["clusters"]])
	else
    		render(view: "portal",model: [q: params?.q])
    }

}
