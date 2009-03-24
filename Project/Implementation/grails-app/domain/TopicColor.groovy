class TopicColor {

	String color
	
    static constraints = {
		color(validator: {
			return cluster ==~ /[0-9a-fA-F]{8}/
		})
    }
}
