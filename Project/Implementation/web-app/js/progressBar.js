var ProgressBar = Class.create({
	offset : -6,
	pStart : 0,
	pEnd : 1,
	element : null,
	pWidth : 0,
	pBarWidth : 0,
	pCornerWidth : 10,
	
	initialize : function(options) {
		this.element = new Element('div',{
			id : options.id,
			className : 'progressWrapper'
		});
		this.pWidth = options.width;
		this.pBarWidth = this.pWidth-2*this.pCornerWidth-this.offset*2+1;
		this.element.setStyle({width: options.width+"px"});
		this.element.update('<div class="progressWrapper_left"></div>'
				   			+'<div class="progressWrapper_center">'
				   			+'  <div id="'+options.id+'_progressBar" class="progressBar_wrapper">'
				   			+'    <div class="progressBar_left"></div>'
				   			+'    <div class="progressBar_center"></div>'
				   			+'    <div class="progressBar_right"></div>'
				   			+'  </div>'
				   			+'</div>'
				   			+'<div class="progressWrapper_right"></div>');
	},
	
	setStart : function(start) {
		this.pStart = start;
		this.update();
	},
	
	setEnd : function(end) {
		this.pEnd = end;
		this.update();
	},
	
	move : function(distance) {
		distance = Math.min(distance, 1 - this.pEnd);
		distance = Math.max(distance, 0 - this.pStart);
		this.pEnd += distance;
		this.pStart += distance;
		this.update();
	},

	update : function() {
		var left   = this.pBarWidth*this.pStart+2*this.offset;
		var width  = this.pBarWidth*(this.pEnd-this.pStart);
		$(this.element.readAttribute("id")+"_progressBar").setStyle({
			left  : left+"px",
			width : width+"px"
		});
	},
	
	getStart : function() {
		return this.pStart;
	},
	
	getEnd : function() {
		return this.pEnd;
	},
	
	toHTML : function() {
		var clone = document.createElement("div");
	    clone.appendChild(this.element.cloneNode(true));
	    return clone.innerHTML;
	}
});

var UndefinedProgressBar = Class.create(ProgressBar, {

	direction : 1,
	step	  : 0.02,
	width 	  : 0.3,
	timeStep  : 0.03,
	pe		  : null,
	
	initialize : function($super,options) {
		$super(options);
	},

	start : function() {
		this.setEnd(this.width);
		this.pe = new PeriodicalExecuter(this.move.bind(this), this.timeStep);
	},

	stop : function() {
		this.pe.stop();
	},
	
	move : function($super) {
		$super(this.step*this.direction)
		if(this.getEnd() >= 1 || this.getStart() <= 0) this.direction *= -1;
	},

	setStart : function($super, start) {
		$super(start);
	},
	
	setEnd : function($super, end) {
		$super(end);
	},
	
	getStart : function($super) {
		return $super();
	},
	
	getEnd : function($super) {
		return $super();
	}
});