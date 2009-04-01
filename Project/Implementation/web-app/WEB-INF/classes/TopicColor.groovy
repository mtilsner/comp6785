class TopicColor implements Comparable {

	String color
	int position
	
    static constraints = {
		color(validator: {
			return cluster ==~ /[0-9a-fA-F]{8}/
		})
    }
	
	int compareTo(obj) {
		obj.position - position
	}
}
