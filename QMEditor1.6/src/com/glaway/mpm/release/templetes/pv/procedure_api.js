/*
 * procedure_api.js 14:49 25.08.2011
 * ver: 0.0 draft
 * ver: 0.1 draft
  + add set_freeze_viewpoint(flag)
  + add get_freeze_viewpoint
  + add get_speed_ratio
  + add on_alert(id) event
 * ver: 0.2 draft
  + add mozilla & cortona 5.0 support
 * ver: 0.3 draft
  + add nonlinear mode
  + add Multi-Thread Animation: 
  		+set_procedure(substeps, position), 
  		+set_multithread_procedure(substeps_arrays, position)
  
  corrected: get_speed_ratio, get_freeze_viewpoint
 * ver: 0.4 draft
  + corrected: set_procedure, set_multithread_procedure
 * ver: 0.5 draft
  + add set_new_viewpoint
 * ver: 0.6 draft
  + add event on_finish_procedure
  + changed _on_vcr_state
 * ver: 0.7 draft
 	+ add fit_to_object(object_name)
 * ver 0.8 draft
 	+ disable _refresh
 * ver 0.9 draft
 	+ vcr_play_ext
 * ver 0.91 draft
 	+ add get_viewpoint_data
 * ver 0.92 draft
 	+ add set_guided_zoom
 * ver 0.925 draft
  + add event on_event_out
  + add event on_start_new_substep
  + add vcr_set_step_fraction
  + add vcr_get_proc_fraction
  + add vcr_get_step_fraction
  + add vcr_get_fraction
  + add set_autostop_mode
  + add vcr_set_position
 * ver 0.926 draft
  + add scene_loaded_on_start
 * ver 0.927 draft
  + add get_procedure_id
  + add get_step_id
  + add get_substep_id
  + improved event on_event_out
 * ver 0.928 draft
  + improved properties: id_procedure, id_step, id_substep
 * ver 0.929
  + add get_steps_array
  + add get_substeps_array
 * ver 0.930
  + add vcr_set_range
 * ver 1.001
  + add support mozilla & cortona 6.0
 * ver 1.1.0.5
  + corrected version number
 * ver 1.2.0.6
  + add flash_node
  + corrected fit_to_object
 * ver 1.2.0.7
  + add set_mute_audio
 * ver 1.3.0.7
  + add fit_to_objects
  + add set_emissiveColor
  + add flash_nodes
 * ver 1.3.0.8
  + corrected set_emissiveColor
 * ver 1.4.0.9
  + fixed wrong order of events (on_start_new_substep before on_simulation_load)
 * ver 1.4.0.10
  + the API is synchronized with the RWI procedure API (include 1.3.0.7 - 1.4.0.9)
 * ver 1.4.0.11
  + set_emissiveColor optimized for mozilla browsers (ATP = 1)
 */

function SimulationAPI(cortona) {
  
  if(_an_api != null) {
    alert('WARNING:\r\n\r\nMultiple simulation API controls not supported');
    return;
  }
  this._ver = "1.4.0.11";
  // API pseudo-constants
  this.STOP = 0;
  this.PLAY = 1;
  this.PAUSE = 2;
  
  // API properties
  this.auto_stop = false;
  
  this.ATP = 0;
  
  this.state = 0;
  this.procedure_fraction = 0;
  this.zoom_level = 0.5;
  
  // current procedures step id
  this.id_procedure = new String();
  this.id_step = new String();
  this.id_substep = new String();
  
  this.ignore_viewpoint_changes = undefined;
  
  this.is_loaded = false;
  
  this.cortona = cortona;
  this.control_script = null;
  
  // stuff
  this._last_alert = new String();
  
  // MultiThread and NonLinear animation mode
  this.isMultiThreadAnimation = false;
  this.AnimationThreads = new Array();
  
  this.is_nonlinear_mode = false;
  this.AllSubSteps = null;
}

// API methods
SimulationAPI.prototype.load = SimulationAPI_load;
SimulationAPI.prototype.unload = SimulationAPI_unload;

SimulationAPI.prototype.scene_loaded_on_start = SimulationAPI_scene_loaded_on_start;

SimulationAPI.prototype.vcr_play = SimulationAPI_vcr_play;
SimulationAPI.prototype.vcr_play_ext = SimulationAPI_vcr_play_ext;
SimulationAPI.prototype.vcr_stop = SimulationAPI_vcr_stop;
SimulationAPI.prototype.vcr_pause = SimulationAPI_vcr_pause;
SimulationAPI.prototype.vcr_next_substep = SimulationAPI_vcr_next_substep;
SimulationAPI.prototype.vcr_previous_substep = SimulationAPI_vcr_previous_substep;
SimulationAPI.prototype.vcr_next_step = SimulationAPI_vcr_next_step;
SimulationAPI.prototype.vcr_previous_step = SimulationAPI_vcr_previous_step;
SimulationAPI.prototype.vcr_goto = SimulationAPI_vcr_goto;
SimulationAPI.prototype.vcr_set_range = SimulationAPI_vcr_set_range;
SimulationAPI.prototype.vcr_set_position = SimulationAPI_vcr_set_position;
SimulationAPI.prototype.vcr_set_fraction = SimulationAPI_vcr_set_fraction;
SimulationAPI.prototype.vcr_set_proc_fraction = SimulationAPI_vcr_set_proc_fraction;
SimulationAPI.prototype.vcr_set_step_fraction = SimulationAPI_vcr_set_step_fraction;

SimulationAPI.prototype.vcr_get_fraction = SimulationAPI_vcr_get_fraction;
SimulationAPI.prototype.vcr_get_proc_fraction = SimulationAPI_vcr_get_proc_fraction;
SimulationAPI.prototype.vcr_get_step_fraction = SimulationAPI_vcr_get_step_fraction;

SimulationAPI.prototype.get_procedure_id = SimulationAPI_get_procedure_id;
SimulationAPI.prototype.get_step_id = SimulationAPI_get_step_id;
SimulationAPI.prototype.get_substep_id = SimulationAPI_get_substep_id;

SimulationAPI.prototype.set_speed_ratio = SimulationAPI_set_speed_ratio;
SimulationAPI.prototype.set_freeze_viewpoint = SimulationAPI_set_freeze_viewpoint;
SimulationAPI.prototype.set_mute_audio = SimulationAPI_set_mute_audio;

SimulationAPI.prototype.get_speed_ratio = SimulationAPI_get_speed_ratio;
SimulationAPI.prototype.get_freeze_viewpoint = SimulationAPI_get_freeze_viewpoint;

SimulationAPI.prototype.set_ui_smoothcontrol = SimulationAPI_set_ui_smoothcontrol;
SimulationAPI.prototype.set_ui_axis = SimulationAPI_set_ui_axis;
SimulationAPI.prototype.set_ui_vcr = SimulationAPI_set_ui_vcr;
SimulationAPI.prototype.set_ui_zoom = SimulationAPI_set_ui_zoom;

SimulationAPI.prototype.set_autostop_mode = SimulationAPI_set_autostop_mode;

SimulationAPI.prototype.zoom_reset = SimulationAPI_zoom_reset;
SimulationAPI.prototype.zoom_set_delta = SimulationAPI_zoom_set_delta;
SimulationAPI.prototype.zoom_set_level = SimulationAPI_zoom_set_level;

SimulationAPI.prototype.set_guided_zoom = SimulationAPI_set_guided_zoom;

SimulationAPI.prototype.set_new_viewpoint = SimulationAPI_set_new_viewpoint;
SimulationAPI.prototype.get_viewpoint_data = SimulationAPI_get_viewpoint_data;
SimulationAPI.prototype.fit_to_object = SimulationAPI_fit_to_object;
SimulationAPI.prototype.fit_to_objects = SimulationAPI_fit_to_objects;
SimulationAPI.prototype._get_node_boundbox = SimulationAPI__get_node_boundbox;
SimulationAPI.prototype.flash_node = SimulationAPI_flash_node;
SimulationAPI.prototype.flash_nodes = SimulationAPI_flash_nodes;
SimulationAPI.prototype.set_emissiveColor = SimulationAPI_set_emissiveColor;
SimulationAPI.prototype._getAllChildren = SimulationAPI__getAllChildren;
SimulationAPI.prototype.checkVisibility = SimulationAPI_checkVisibility;

SimulationAPI.prototype.get_steps_array = SimulationAPI_get_steps_array;
SimulationAPI.prototype.get_substeps_array = SimulationAPI_get_substeps_array;

SimulationAPI.prototype.interactivity_xml_load = SimulationAPI_interactivity_xml_load;
SimulationAPI.prototype.interactivity_xml_get_task = SimulationAPI_interactivity_xml_get_task;
SimulationAPI.prototype.interactivity_xml_get_subtasks = SimulationAPI_interactivity_xml_get_subtasks;

// New API methods for nonlinear_mode
SimulationAPI.prototype.set_procedure = SimulationAPI_set_procedure;
SimulationAPI.prototype.set_multithread_procedure = SimulationAPI_set_multithread_procedure;

// service methods
SimulationAPI.prototype._prepare_nonlinear_mode = SimulationAPI__prepare_nonlinear_mode;
SimulationAPI.prototype._preset_to_position = SimulationAPI__preset_to_position;
SimulationAPI.prototype._form_substeps_list = SimulationAPI__form_substeps_list;
SimulationAPI.prototype._add_animation_substep = SimulationAPI__add_animation_substep;
SimulationAPI.prototype._clear_multithread_mode = SimulationAPI__clear_multithread_mode;
SimulationAPI.prototype._prepare_multithread_animations = SimulationAPI__prepare_multithread_animations;
SimulationAPI.prototype._corect_procedure_duration = SimulationAPI__corect_procedure_duration;
SimulationAPI.prototype._get_animation_duration = SimulationAPI__get_animation_duration;
SimulationAPI.prototype._refresh = SimulationAPI__refresh;
// Stuff

SimulationAPI.prototype._check_alert = SimulationAPI__check_alert;
SimulationAPI.prototype._create_vector = SimulationAPI__create_vector;
SimulationAPI.prototype._createArrayFromField = SimulationAPI__createArrayFromField;
SimulationAPI.prototype._advise_events = SimulationAPI__advise_events;

// events
/*
this.on_simulation_load(success)
this.on_simulation_unload() (?)
this.on_start_substep(procid, stepid, substepid)
this.on_start_new_substep(procid, stepid, substepid)
this.on_vcr_state(state)
this.on_proc_fraction_changed(fraction)
this.on_zoom_changed(zoomlevel)
this.on_alert(id)
this.on_finish_procedure()

this.on_event_out(events)
*/

var _an_api = null;

/* Cortona automation event handlers */

function _on_scene_loaded(url, nodes, protos, error) {
  var success = (error == '');
  _an_api.is_loaded = false;

  if(success) {
  	var e = _an_api.cortona.Engine;
  	e.ValidateNodes = 1;
  	e.AutoRefresh = 1;
  	if (nodes.Count > 0) {
  		try {
    	    e.RootNodes.Clear();
        	e.RootNodes.Add(nodes);
	        _an_api.control_script = e.Nodes.Item('VCR_CONTROL_SCRIPT');
			var usedskin = _an_api.cortona.skin;
			_an_api.cortona.skin="{1706B265-E103-4332-9871-7FEE6C37C699}"; //Default Skin
			_an_api.cortona.refresh();
			_an_api.cortona.skin=usedskin;
	        _an_api.id_procedure = _an_api.get_procedure_id();
			_an_api.id_step = _an_api.get_step_id();
			_an_api.id_substep = _an_api.get_substep_id();
			_an_api.is_loaded = true;
  		} catch(exception) {
  		  e.RootNodes.Clear();
  		}
  		try {
  		  if(typeof(_an_api.ignore_viewpoint_changes) == 'undefined')
  		    _an_api.ignore_viewpoint_changes = false;
  			_an_api.control_script.Fields.Item('set_ignore_viewpoints').Value = _an_api.ignore_viewpoint_changes;
  		} catch(exception) {}
  	}
  }
  if('on_simulation_load' in _an_api){
		_an_api.on_simulation_load(_an_api.is_loaded);
		_an_api._advise_events();
	}
}

function SimulationAPI__advise_events() {
	try {
		_an_api.control_script.Fields.Item('on_start_substep').Advise(new Function('value, hint, timeStamp', 'var s0, s1, s2; try { s0 = value.GetValue(0); s1 = value.GetValue(1); s2 = value.GetValue(2); } catch(exception) { s0 = value.GetValue(0); s1 = value.GetValue(1); s2 = value.GetValue(2); } if(s1 == null) s1 = ""; if(s2 == null) s2 = ""; if(s0 == null) s0 = ""; if("on_start_substep" in _an_api) _an_api.on_start_substep(s0, s1, s2); _an_api._check_alert(s2);'), 0); 
		_an_api.control_script.Fields.Item('on_start_new_substep').Advise(new Function('value, hint, timeStamp', 'var s0, s1, s2; try { s0 = value.GetValue(0); s1 = value.GetValue(1); s2 = value.GetValue(2); } catch(exception) { s0 = value.GetValue(0); s1 = value.GetValue(1); s2 = value.GetValue(2); } if(s1 == null) s1 = ""; if(s2 == null) s2 = ""; if(s0 == null) s0 = ""; if("on_start_new_substep" in _an_api) _an_api.on_start_new_substep(s0, s1, s2); _an_api.id_procedure = s0; _an_api.id_step = s1; _an_api.id_substep = s2;'), 0); 
		try{
			_an_api.control_script.Fields.Item('on_new_state').Advise(_on_new_state, 0);
		}catch(err){
			_an_api.control_script.Fields.Item('on_vcr_state').Advise(_on_vcr_state, 0);
		}
		_an_api.control_script.Fields.Item('on_proc_fraction_changed').Advise(new Function('value, hint, timeStamp', 'if("on_proc_fraction_changed" in _an_api) _an_api.on_proc_fraction_changed(value.Value); _an_api.procedure_fraction = value.Value;'), 0);
		_an_api.control_script.Fields.Item('on_zoom_changed').Advise(new Function('value, hint, timeStamp', 'if("on_zoom_changed" in _an_api) _an_api.on_zoom_changed(value.Value); _an_api.zoom_level = value.Value;'), 0);
		try{
			_an_api.control_script.Fields.Item('on_procedure_events').Advise(_on_procedure_events, 0);
		}catch(err){
			_an_api.control_script.Fields.Item('on_event_out').Advise(_on_event_out, 0);
		}
	}
	catch(exception) {
	}
}

function SimulationAPI__check_alert(sId) {
	if((this._last_alert != sId) && (this.state == this.PLAY)) {
		this._last_alert = sId;
		if((this._last_alert != '') && (this._last_alert.indexOf('_')>=0))
      if('on_alert' in this)
        this.on_alert(this._last_alert);
	}
}

function SimulationAPI_load(url) {
  _an_api = this;
  this.cortona.Engine.CreateVrmlFromUrl(url, _on_scene_loaded);
}

function SimulationAPI_scene_loaded_on_start() {
  _an_api = this;
  _an_api.is_loaded = false;
  var e = _an_api.cortona.Engine;
  try {
	  _an_api.control_script = e.Nodes.Item('VCR_CONTROL_SCRIPT');
	  _an_api.id_procedure = _an_api.get_procedure_id();
	  _an_api.id_step = _an_api.get_step_id();
	  _an_api.id_substep = _an_api.get_substep_id();
	  _an_api.is_loaded = true;
	}
	catch(exception) {
		e.RootNodes.Clear();
	}
  try {
  		  if(typeof(_an_api.ignore_viewpoint_changes) == 'undefined')
  		    _an_api.ignore_viewpoint_changes = false;
  			_an_api.control_script.Fields.Item('set_ignore_viewpoints').Value = _an_api.ignore_viewpoint_changes;
  		} catch(exception) {}
  if('on_simulation_load' in _an_api){
		_an_api.on_simulation_load(_an_api.is_loaded);
		_an_api._advise_events();
	}
}

function SimulationAPI_unload() {
  this.is_loaded = false;
  this.cortona.Engine.RootNodes.Clear();
}
function SimulationAPI_vcr_play() {
	if(this.is_loaded){
		if(this.control_script.Fields.Item('isFinal').Value!=0)this.vcr_stop();
		this.vcr_play_ext();
	}
}
function SimulationAPI_vcr_play_ext() {
	if(this.is_loaded){
		if(this.control_script.TypeName=="Script")
			if(this.control_script.Fields.Item('isFinal').Value && (this.control_script.Fields.Item('cmd_stack').Count==0))
				return;

		this.control_script.Fields.Item('vcr_play').Value = 0;
		if(this.isMultiThreadAnimation)
			for(var i=0; i<this.AnimationThreads.length; i++)this.AnimationThreads[i].Fields.Item('vcr_play').Value = 0;
	}
}
function SimulationAPI_vcr_stop() {
	if(this.is_loaded){
		this.control_script.Fields.Item('vcr_stop').Value = 0;
		if(this.isMultiThreadAnimation)
			for(var i=0; i<this.AnimationThreads.length; i++)this.AnimationThreads[i].Fields.Item('vcr_stop').Value = 0;
	}
}
function SimulationAPI_vcr_pause() {
	if(this.is_loaded){
		this.control_script.Fields.Item('vcr_pause').Value = 0;
		if(this.isMultiThreadAnimation)
			for(var i=0; i<this.AnimationThreads.length; i++)this.AnimationThreads[i].Fields.Item('vcr_pause').Value = 0;
	}
}
function SimulationAPI_vcr_next_step() {
	if(this.is_loaded){
		this.control_script.Fields.Item('vcr_forward').Value = 0;
		if(this.isMultiThreadAnimation)
			for(var i=0; i<this.AnimationThreads.length; i++)this.AnimationThreads[i].Fields.Item('vcr_forward').Value = 0;
	}
}
function SimulationAPI_vcr_previous_step() {
	if(this.is_loaded){
		this.control_script.Fields.Item('vcr_backward').Value = 0;
		if(this.isMultiThreadAnimation)
			for(var i=0; i<this.AnimationThreads.length; i++)this.AnimationThreads[i].Fields.Item('vcr_backward').Value = 0;
	}
}
function SimulationAPI_vcr_next_substep() {
	if(this.is_loaded){
		this.control_script.Fields.Item('vcr_next').Value = 0;
		if(this.isMultiThreadAnimation)
			for(var i=0; i<this.AnimationThreads.length; i++)this.AnimationThreads[i].Fields.Item('vcr_next').Value = 0;
	}
}
function SimulationAPI_vcr_previous_substep() {
	if(this.is_loaded){
		this.control_script.Fields.Item('vcr_previous').Value = 0;
		if(this.isMultiThreadAnimation)
			for(var i=0; i<this.AnimationThreads.length; i++)this.AnimationThreads[i].Fields.Item('vcr_previous').Value = 0;
	}
}
function SimulationAPI_vcr_goto(id) {
	if(this.is_loaded){
		this.control_script.Fields.Item('vcr_goto').Value = new String(id);
		if(this.isMultiThreadAnimation)
			for(var i=0; i<this.AnimationThreads.length; i++)this.AnimationThreads[i].Fields.Item('vcr_goto').Value = new String(id);
	}
}
function SimulationAPI_vcr_set_range(sStartSubstepID, sEndSubstepID) {
	if(this.is_loaded){
		var rng = this.cortona.Engine.CreateField('MFString');
		rng.Add(new String(sStartSubstepID));
		rng.Add(new String(sEndSubstepID));
		this.control_script.Fields.Item('vcr_set_range').Assign(rng);
	}
}
function SimulationAPI_vcr_set_position(id) {
	if(this.is_loaded){
		this.control_script.Fields.Item('vcr_set_position').Value = new String(id);
		if(this.isMultiThreadAnimation)
			for(var i=0; i<this.AnimationThreads.length; i++)this.AnimationThreads[i].Fields.Item('vcr_set_position').Value = new String(id);
	}
}
function SimulationAPI_vcr_set_fraction(value) {
	if(this.is_loaded){
		this.control_script.Fields.Item('vcr_set_fraction').Value = new Number(value);
		if(this.isMultiThreadAnimation)
			for(var i=0; i<this.AnimationThreads.length; i++)this.AnimationThreads[i].Fields.Item('vcr_set_fraction').Value = new Number(value); 
	}
}
function SimulationAPI_vcr_set_proc_fraction(value) {
	if(this.is_loaded){
			this.control_script.Fields.Item('vcr_set_proc_fraction').Value = new Number(value);
			if(this.isMultiThreadAnimation)
				for(var i=0; i<this.AnimationThreads.length; i++)this.AnimationThreads[i].Fields.Item('vcr_set_proc_fraction').Value = new Number(value);
	}
}
function SimulationAPI_vcr_set_step_fraction(value) {
	if(this.is_loaded){
			this.control_script.Fields.Item('vcr_set_step_fraction').Value = new Number(value);
			if(this.isMultiThreadAnimation)
				for(var i=0; i<this.AnimationThreads.length; i++)this.AnimationThreads[i].Fields.Item('vcr_set_step_fraction').Value = new Number(value);
	}
}
function SimulationAPI_vcr_get_fraction() {
	if(this.is_loaded){
		var f=0;
		try{f = this.control_script.Fields.Item('on_substep_fraction_changed').Value;}
		catch(e){}
		return f;
	}
}
function SimulationAPI_vcr_get_proc_fraction() {
	if(this.is_loaded){
		var f=0;
		try{f = this.control_script.Fields.Item('on_proc_fraction_changed').Value;}
		catch(e){}
		return f;
	}
}
function SimulationAPI_vcr_get_step_fraction() {
	if(this.is_loaded){
		var f=0;
		try{f = this.control_script.Fields.Item('on_step_fraction_changed').Value;}
		catch(e){}
		return f;
	}
}
function SimulationAPI_get_procedure_id() {
	var f="";
	if(this.is_loaded){
		try{f = this.control_script.Fields.Item('on_start_new_substep').GetValue(0);}
		catch(e){}
	}
	return f;
}
function SimulationAPI_get_step_id() {
	var f="";
	if(this.is_loaded){
		try{f = this.control_script.Fields.Item('on_start_new_substep').GetValue(1);}
		catch(e){}
	}
	return f;
}
function SimulationAPI_get_substep_id() {
	var f="";
	if(this.is_loaded){
		try{f = this.control_script.Fields.Item('on_start_new_substep').GetValue(2);}
		catch(e){}
	}
	return f;
}

function SimulationAPI_set_speed_ratio(value) {
	if(this.is_loaded){
			this.control_script.Fields.Item('set_speed_ratio').Value = new Number(value);
			if(this.isMultiThreadAnimation)
				for(var i=0; i<this.AnimationThreads.length; i++)this.AnimationThreads[i].Fields.Item('set_speed_ratio').Value = new Number(value);
	}
}
function SimulationAPI_set_freeze_viewpoint(value) { if(this.is_loaded) this.control_script.Fields.Item('set_ignore_viewpoints').Value = value ? 1 : 0; }
function SimulationAPI_set_mute_audio(value) { if(this.is_loaded){try{this.control_script.Fields.Item('set_mute_audio').Value = value ? 1 : 0; return true;}catch(err){return false;}} }

function SimulationAPI_get_speed_ratio() { if(this.is_loaded) return this.control_script.Fields.Item('timeCompression').Value; return 1; }
function SimulationAPI_get_freeze_viewpoint() { if(this.is_loaded) return this.control_script.Fields.Item('blockViewpoints').Value; return false; }

function SimulationAPI_set_ui_smoothcontrol(flag) { if(this.is_loaded) this.control_script.Fields.Item('set_ui_smoothcontrol').Value = flag ? 1 : 0;}
function SimulationAPI_set_ui_axis(flag) { if(this.is_loaded) this.control_script.Fields.Item('set_ui_axis').Value = flag ? 1 : 0; }
function SimulationAPI_set_ui_vcr(flag) { if(this.is_loaded) this.control_script.Fields.Item('set_ui_vcr').Value = flag ? 1 : 0; }
function SimulationAPI_set_ui_zoom(flag) { if(this.is_loaded) this.control_script.Fields.Item('set_ui_zoom').Value = flag ? 1 : 0; }
function SimulationAPI_set_autostop_mode(value) {
	if(this.is_loaded){
		 this.control_script.Fields.Item('set_stop_by_step').Value = (value==1);
		 this.control_script.Fields.Item('set_stop_by_substep').Value = (value==2);
	}
}
function SimulationAPI_zoom_reset() { if(this.is_loaded) this.control_script.Fields.Item('zoom_reset').Value = 0; }
function SimulationAPI_zoom_set_delta(value) { if(this.is_loaded) this.control_script.Fields.Item('zoom_set_delta').Value = new Number(value); }
function SimulationAPI_zoom_set_level(value) { if(this.is_loaded) this.control_script.Fields.Item('zoom_set_level').Value = new Number(value); }

function SimulationAPI_set_guided_zoom(flag) { if(this.is_loaded) this.control_script.Fields.Item('isEnabledSkinControl').Value = flag ? 1 : 0; }

function SimulationAPI_set_new_viewpoint(value) {
	if(this.is_loaded){
		var a = this.cortona.Engine.CreateField('MFFloat');
		for(var i=0;i<value.length;i++)
				a.Add(new Number(value[i]));
		this.control_script.Fields.Item('set_new_viewpoint').Assign(a);
	}
}

function SimulationAPI_get_steps_array() {
	var IDs = [];
	if(this.is_loaded){
		try{
			var steps = this.control_script.Fields.Item('OP').Value.Fields.Item("steps");
			for(var i=0; i<steps.Count; i++){
				IDs.push(steps.GetValue(i).Fields.Item('id').Value);
			}
		}catch(e){}
	}
	return IDs;
}
function SimulationAPI_get_substeps_array() {
	var IDs = [];
	if(this.is_loaded){
		try{
			var steps = this.control_script.Fields.Item('OP').Value.Fields.Item("steps");
			for(var i=0; i<steps.Count; i++){
				var substeps = steps.GetValue(i).Fields.Item("substeps");
				for(var j=0; j<substeps.Count; j++){
					IDs.push(substeps.GetValue(j).Fields.Item('id').Value);
				}
			}
		}catch(e){}
	}
	return IDs;
}

function SimulationAPI_interactivity_xml_load(url) {}
function SimulationAPI_interactivity_xml_get_task() { return ''; }
function SimulationAPI_interactivity_xml_get_subtasks() { return new Array(); }

function _on_new_state(value, hint, timeStamp){
var oldstate = _an_api.state;
if(!_an_api.isMultiThreadAnimation){
	if("on_vcr_state" in _an_api)_an_api.on_vcr_state(parseInt(value.GetValue(0)));
		_an_api.state = value.GetValue(0);
}else{
	var tmpState = 0;
	for(var i=0; i<_an_api.AnimationThreads.length; i++){
		if(_an_api.AnimationThreads[i].Fields.Item('on_new_state').GetValue(0)=="1"){
			tmpState = 1;
			break;
		} else {
				if(_an_api.AnimationThreads[i].Fields.Item('on_new_state').GetValue(0)=="2")
				tmpState = 2;
		}
	}
	if(this.state!=tmpState){
		if("on_vcr_state" in _an_api)_an_api.on_vcr_state(tmpState);
		_an_api.state = tmpState;
	}
}
if("on_finish_procedure" in _an_api){
		if((oldstate == _an_api.PLAY)&&(_an_api.state == _an_api.STOP)&&(value.GetValue(1)!="0"))
			setTimeout("_an_api.on_finish_procedure();", 0);
}

_an_api._check_alert(_an_api.id_substep);
}

function _on_vcr_state(value, hint, timeStamp){
var oldstate = _an_api.state;
if(!_an_api.isMultiThreadAnimation){
	if("on_vcr_state" in _an_api)_an_api.on_vcr_state(value.Value);
		_an_api.state = value.Value;
}else{
	var tmpState = 0;
	for(var i=0; i<_an_api.AnimationThreads.length; i++){
		if(_an_api.AnimationThreads[i].Fields.Item('on_vcr_state').Value==1){
			tmpState = 1;
			break;
		} else {
				if(_an_api.AnimationThreads[i].Fields.Item('on_vcr_state').Value==2)
				tmpState = 2;
		}
	}
	if(this.state!=tmpState){
		if("on_vcr_state" in _an_api)_an_api.on_vcr_state(tmpState);
		_an_api.state = tmpState;
	}
}
if("on_finish_procedure" in _an_api){
		if((oldstate == _an_api.PLAY)&&(_an_api.state == _an_api.STOP)&&(_an_api.control_script.Fields.Item('isFinal').Value!=0))
			setTimeout("_an_api.on_finish_procedure();", 0);
}

_an_api._check_alert(_an_api.id_substep);
}

function _on_event_out(value, hint, timeStamp){
if("on_events_out" in _an_api){
	var events_array = new Array();
	var CommandsSetID = _an_api.control_script.Nodes.Item("VCR_CONTROL_SCRIPT").Fields.Item('CommandsSetID').Value.Fields.Item('_');
	for(var i=0; i<CommandsSetID.Count; i++)
		events_array.push({id: CommandsSetID.GetValue(i).Fields.Item('ID').Value, title: CommandsSetID.GetValue(i).Fields.Item('title').Value, info: CommandsSetID.GetValue(i).Fields.Item('info').Value});
	_an_api.on_events_out(events_array);
}
}
function _on_procedure_events(value, hint, timeStamp){
if("on_events_out" in _an_api){
	var events_array = new Array();
	for(var i=0; i<value.Count; i+=3)
		events_array.push({id: value.GetValue(i), title: value.GetValue(i+1), info: value.GetValue(i+2)});
	_an_api.on_events_out(events_array);
}
}

//==========================
function SimulationAPI__prepare_nonlinear_mode() {
	try{
		if(parseFloat(this.control_script.Fields.Item('version_num').Value)<3){
			alert('WARNING:\r\n\r\nOld version simulation. Nonlinear mode not supported.');
	    	return 0;
	    }
		var e = this.cortona.Engine;
		var OP = this.control_script.Fields.Item('newproc');
		this.AllSubSteps = this.control_script.Fields.Item('AllSubSteps').Value.Fields.Item('children');
		var newproc = e.CreateVrmlFromString('PROTO Procedure[field SFString title "" field SFString comment "" exposedField SFString id "" exposedField MFNode steps []]{Group{children IS steps}}PROTO Step[field SFString title "" exposedField SFString id "" exposedField SFBool simulate TRUE exposedField MFNode substeps []]{Group{children IS substeps}}Procedure{steps Step{substeps[]}}');
		OP.Value=newproc.GetValue(0);
		this.is_nonlinear_mode=true;
		return 1;
	} catch(exception) {return 0;}
}

function SimulationAPI_set_procedure(substeps, position) {
if(this.is_loaded){
	this.vcr_pause();
	if(this._prepare_nonlinear_mode()==0) return 0;
	this._clear_multithread_mode();
	var substeps_count = this._form_substeps_list(substeps, this.control_script);
	this._refresh();
	if(substeps_count>0){
		this._preset_to_position(position);
		return 1;
	}
}
return 0;
}

function SimulationAPI_set_multithread_procedure(substeps_arrays, position) {
if(this.is_loaded){
	if(substeps_arrays.length<=1){
		if(substeps_arrays.length==0)return this.set_procedure(new Array(), position);
			else return this.set_procedure(substeps_arrays[0], position);
	}
	this.vcr_pause();
	if(this._prepare_nonlinear_mode()==0) return 0;
	this.isMultiThreadAnimation=true;
	this._prepare_multithread_animations(substeps_arrays.length);

	var substeps_count = 0;
	var isPresentSomeSubstep = false;

	substeps_count = this._form_substeps_list(substeps_arrays[0], this.control_script);
	if(substeps_count>0)isPresentSomeSubstep=true;
	for(var i=0; i<this.AnimationThreads.length; i++){
		substeps_count = this._form_substeps_list(substeps_arrays[i+1], this.AnimationThreads[i]);
		if(substeps_count>0)isPresentSomeSubstep=true;
	}
	if(isPresentSomeSubstep){
		this._refresh();
		this._corect_procedure_duration();
		this._preset_to_position(position);
		return 1;
	}

}
return 0;
}

function SimulationAPI__corect_procedure_duration() {
if(this.isMultiThreadAnimation){
	var base_animation_duration=this._get_animation_duration(this.control_script);
	var max_duration = 0;
	var duration = 0;
	var duration_array = new Array();
	var NullSubStep = null;
	var strNullSubStep = 'PROTO Set_NULL[field SFString title "" field MFFloat Parameters [] exposedField MFFloat period [0,1,0,0,1] eventIn SFFloat time_fraction  exposedField SFInt32 objectID 0 exposedField SFInt32 attributeID 0 exposedField SFString objectName "" exposedField SFString attributeName "null" eventOut SFVec3f value_changed ]{Group{}}PROTO SubStepNULL[exposedField MFNode commands [] field SFString title "NULL" exposedField SFString id "6FEB6759-47D8-43EC-89D0-4AB3D0A1A287" exposedField SFTime duration 1]{Group{children IS commands}}SubStepNULL{commands Set_NULL{}}';
	for(var i=0; i<this.AnimationThreads.length; i++){
		duration=substeps_count = this._get_animation_duration(this.AnimationThreads[i]);
		duration_array.push(duration);
		if((duration>max_duration)&&(duration>base_animation_duration)) max_duration=duration;
	}
	if(max_duration>0){
		duration = max_duration - base_animation_duration;
		NullSubStep = this.cortona.Engine.CreateVrmlFromString(strNullSubStep);
		this.control_script.Fields.Item('newproc').Value.Fields.Item('steps').GetValue(0).Fields.Item('substeps').Add(NullSubStep.GetValue(0));
		NullSubStep.GetValue(0).Fields.Item('duration').Value=duration;
	}else{
		max_duration=base_animation_duration;
	}
	for(var z=0; z<this.AnimationThreads.length; z++){
		if(max_duration>duration_array[z]+0.01){
			duration = max_duration - duration_array[z];
			NullSubStep = this.cortona.Engine.CreateVrmlFromString(strNullSubStep);
			this.AnimationThreads[z].Fields.Item('newproc').Value.Fields.Item('steps').GetValue(0).Fields.Item('substeps').Add(NullSubStep.GetValue(0));
			NullSubStep.GetValue(0).Fields.Item('duration').Value=duration;
		}
	}
}
}

function SimulationAPI__get_animation_duration(animation_object) {
var CurrentSubSteps = animation_object.Fields.Item('newproc').Value.Fields.Item('steps').GetValue(0).Fields.Item('substeps');
var totalduration = 0;
for(i=0; i<CurrentSubSteps.Count; i++)
	totalduration+=CurrentSubSteps.GetValue(i).Fields.Item('duration').Value;
return totalduration;
}

function SimulationAPI__clear_multithread_mode() {
if(this.isMultiThreadAnimation){
this.isMultiThreadAnimation=false;
	for(var i=0; i<this.AnimationThreads.length; i++){
		this.cortona.Engine.RootNodes.Remove('animation_node_N'+i);
	}
	this.AnimationThreads = new Array();
}
}


function SimulationAPI__prepare_multithread_animations(n) {
var count = n-1;
var animation_proto = this.control_script.Proto;
var animation_node = null;
var a = this.AnimationThreads.length;
var newproc = null;
if(a<count){
	for(var i=a; i<count; i++){
		animation_node = animation_proto.CreateInstance();
		this.cortona.Engine.RootNodes.Add(animation_node);
		animation_node.Name = 'animation_node_N'+i;
		try{
			animation_node.Fields.Item('on_new_state').Advise(_on_new_state, 0);
		}catch(err){
			animation_node.Fields.Item('on_vcr_state').Advise(_on_vcr_state, 0);
		}
		this.cortona.Engine.AddRoute(this.control_script, 'outPlayPause', animation_node, 'inPlayPause');
		this.cortona.Engine.AddRoute(this.control_script, 'onSliderMove', animation_node, 'setSliderPosition');
		newproc = this.cortona.Engine.CreateVrmlFromString('PROTO Procedure[field SFString title "" field SFString comment "" exposedField SFString id "" exposedField MFNode steps []]{Group{children IS steps}}PROTO Step[field SFString title "" exposedField SFString id "" exposedField SFBool simulate TRUE exposedField MFNode substeps []]{Group{children IS substeps}}Procedure{steps Step{substeps[]}}');
		animation_node('newproc').Value=newproc.GetValue(0);
		this.AnimationThreads.push(animation_node);
	}
}
if(a>count){
for(var i=0; i<count; i++)this.AnimationThreads.push(animation_node);
	for(var i=count; i<a; i++){
		this.cortona.Engine.RootNodes.Remove('animation_node_N'+i);
		this.AnimationThreads.pop();
	}
}
}

function SimulationAPI__form_substeps_list(substeps, animation_object) {
var ActiveSubSteps = this.cortona.Engine.CreateField('MFNode');
if(substeps.length>0){
	var substep_number = 0;
	for(var i=0; i<substeps.length; i++){
		substep_number=this._add_animation_substep(substeps[i]);
		if(substep_number>=0){
			ActiveSubSteps.Add(this.AllSubSteps.GetValue(substep_number));
		}else alert('WARNING:\r\n\r\nAnimation with ID - '+substeps[i]+' - not found.');
	}
}
animation_object.Fields.Item('newproc').Value.Fields.Item('steps').GetValue(0).Fields.Item('substeps').Assign(ActiveSubSteps);
return ActiveSubSteps.Count;
}

function SimulationAPI__add_animation_substep(substep) {
for(var z=0; z<this.AllSubSteps.Count; z++)
	if(this.AllSubSteps.GetValue(z).Fields.Item("id").Value==substep)
		return z;
return -1;
}


function SimulationAPI__preset_to_position(position) {
var step = 0;
var substep = 0;
var fraction = 0;
var a = this.cortona.Engine.CreateField('MFFloat');
if(position>0){
	substep = this.control_script.Fields.Item('newproc').Value.Fields.Item('steps').GetValue(0).Fields.Item('substeps').Count-1;
	fraction = 1;
}
this.control_script.Fields.Item('set_new_procedure').Assign(this.control_script.Fields.Item('newproc'));
a.Add(step);
a.Add(substep);
a.Add(fraction);
this.control_script.Fields.Item('now_step_substep_fraction').Assign(a);
if(this.isMultiThreadAnimation){
	for(var i=0; i<this.AnimationThreads.length; i++){
		if(position>0)a.GetValue(1)=this.AnimationThreads[i].Fields.Item('newproc').Value.Fields.Item('steps').GetValue(0).Fields.Item('substeps').Count-1;
		this.AnimationThreads[i].Fields.Item('set_new_procedure').Assign(this.AnimationThreads[i].Fields.Item('newproc'));
		this.AnimationThreads[i].Fields.Item('now_step_substep_fraction').Assign(a);
	}
}
this._refresh();
}

function SimulationAPI__refresh() {
//this.cortona.refresh();
}

function SimulationAPI__create_vector(x, y, z) {
	var e = this.cortona.Engine;
	var tMatrix = e.CreatePosition(e.CreateNodeFromString("Transform{translation "+x+" "+y+" "+z+"}"));
	return tMatrix.Translation;
}
function SimulationAPI__createArrayFromField(f) {
	var arr = [];
    switch(f.Type) {
      case 210: // SFRotation
      	arr=[f.X, f.Y, f.Z, f.Angle];
      	break;
      case 213: // SFVec3f
      	arr=[f.X, f.Y, f.Z];
      	break;
      case 212: // SFVec2f
        arr=[f.X, f.Y];
        break;
    }
  return arr;
}

function SimulationAPI__get_node_boundbox(n) {
	var bc = n.BBoxCenter;
	var bs = n.BBoxSize;
    if((bs.X==0)&&(bs.Y==0)&&(bs.Z==0))return [];
    var e = this.cortona.Engine;
    var vCenter = this._create_vector(bc.X, bc.Y, bc.Z);
	var v1 = this._create_vector(bc.X+bs.X, bc.Y+bs.Y, bc.Z+bs.Z);
	var v2 = this._create_vector(bc.X-bs.X, bc.Y+bs.Y, bc.Z+bs.Z);
	var v3 = this._create_vector(bc.X+bs.X, bc.Y-bs.Y, bc.Z+bs.Z);
	var v4 = this._create_vector(bc.X-bs.X, bc.Y-bs.Y, bc.Z+bs.Z);
	var v5 = this._create_vector(bc.X+bs.X, bc.Y+bs.Y, bc.Z-bs.Z);
	var v6 = this._create_vector(bc.X-bs.X, bc.Y+bs.Y, bc.Z-bs.Z);
	var v7 = this._create_vector(bc.X+bs.X, bc.Y-bs.Y, bc.Z-bs.Z);
	var v8 = this._create_vector(bc.X-bs.X, bc.Y-bs.Y, bc.Z-bs.Z);

    var transMatrix = e.CreatePosition(null);

    try{
	    while(n.Fields.Item("parent").Value!=null){
	    		if(n.Fields.Item("whichChoice") && n.Fields.Item("whichChoice").Value<0)return [];
	    		n=n.Fields.Item("parent").Value;
	    		transMatrix.SetTransform(n);
	    		vCenter=transMatrix.MultVecMatrix(vCenter);
	    		v1=transMatrix.MultVecMatrix(v1);
	    		v2=transMatrix.MultVecMatrix(v2);
	    		v3=transMatrix.MultVecMatrix(v3);
	    		v4=transMatrix.MultVecMatrix(v4);
	    		v5=transMatrix.MultVecMatrix(v5);
	    		v6=transMatrix.MultVecMatrix(v6);
	    		v7=transMatrix.MultVecMatrix(v7);
	    		v8=transMatrix.MultVecMatrix(v8);
	    }
    } catch(exception) {}
    return [vCenter, v1, v2, v3, v4, v5, v6, v7, v8];
}

function SimulationAPI_fit_to_object(name) {
	this.fit_to_objects([name]);
}

function SimulationAPI_fit_to_objects(aNames) {
	var bbxs = [];
	var e = this.cortona.Engine;
	for(var i=0; i<aNames.length; i++){
	    try{
	    	bbxs.push(this._get_node_boundbox(e.Nodes.Item(aNames[i])));
	    } catch(exception) {}
    }
    var mmX = [];
    var mmY = [];
    var mmZ = [];

    for(var i=0; i<bbxs.length; i++){
    	var crds = bbxs[i];
    	for(var j=1; j<crds.length; j++){
    		if(mmX.length>0){
    			mmX[0]=Math.min(mmX[0], crds[j].X);
    			mmX[1]=Math.max(mmX[1], crds[j].X);
    			mmY[0]=Math.min(mmY[0], crds[j].Y);
    			mmY[1]=Math.max(mmY[1], crds[j].Y);
    			mmZ[0]=Math.min(mmZ[0], crds[j].Z);
    			mmZ[1]=Math.max(mmZ[1], crds[j].Z);
    		}else{
    			mmX=[crds[j].X, crds[j].X];
    			mmY=[crds[j].Y, crds[j].Y];
    			mmZ=[crds[j].Z, crds[j].Z];
    		}
    	}
    }
    if(mmX.length==0)return;
    var vCenter = this._create_vector((mmX[0]+mmX[1])/2, (mmY[0]+mmY[1])/2, (mmZ[0]+mmZ[1])/2);
    var maxSize = 0;
    maxSize = Math.max(Math.abs(mmX[0]-mmX[1]), Math.max(Math.abs(mmY[0]-mmY[1]), Math.abs(mmZ[0]-mmZ[1])));
    var sbs = e.BBoxSize;
	var SceneSize=Math.sqrt(sbs.X*sbs.X+sbs.Y*sbs.Y+sbs.Z*sbs.Z);
	if(0.45*SceneSize<maxSize)maxSize=0.45*SceneSize;
	var zoomk = 2;
  	var k = 0.828427; // 2*Math.tan(0.785398/2)
  	var zoom = maxSize*zoomk/k;

    var mr = e.CreatePosition(null);
    mr.Rotation = e.CameraPosition.Rotation;

    var mc = e.CreatePosition(null);
    mc.Translation = this._create_vector(vCenter.X,vCenter.Y,vCenter.Z);

    var pos = mc.MultVecMatrix(mr.Inverse().MultMatrixVec(this._create_vector(0, 0, zoom)));

    var posArray = this._createArrayFromField(pos);
    var oriArray = this._createArrayFromField(e.CameraPosition.Rotation);
	var minZoom = 0.2*maxSize/k;
	var maxZoom = SceneSize/k;
	var arrVP = new Array(vCenter.X,vCenter.Y,vCenter.Z,posArray[0],posArray[1],posArray[2],oriArray[0],oriArray[1],oriArray[2],oriArray[3],minZoom,maxZoom,0);
	_an_api.set_new_viewpoint(arrVP);
}

function SimulationAPI_flash_node(sNode) {
	this.flash_nodes([sNode]);
}
function SimulationAPI_flash_nodes(aNodes) {
	var e = _an_api.cortona.Engine;
	var flasher=null;
	try{flasher=e.Nodes.Item("VCR__PROTO__FLASHER");}catch(err){}
	if(flasher==null){
		flasher=e.CreateVrmlFromString('PROTO flasher[eventIn MFNode node]{DEF TS TimeSensor {loop TRUE enabled FALSE}DEF SC Script{eventIn	SFTime inT eventIn MFNode inN IS node field SFNode tmr USE TS field MFNode ns []field MFNode ons []field SFTime st -1 field SFTime itv 1 field SFInt32 times 2 field SFBool actv FALSE field MFColor cs [] field MFColor ocs [] field SFTime tic -1 url "javascript: function getNs(val){ns.length=0;var t=0;var chs = new Array();for(var i=0; i<val.length; i++){chs.push(val[i]);}for(var i=0; i<chs.length; i++){if(chs[i] && chs[i].children){var ch = chs[i].children;for(var j=0; j<ch.length; j++){chs.push(ch[j]);}}if(chs[i] && chs[i].appearance && chs[i].appearance.material){ns[t]=chs[i];t++;}}}function inN(value){if(actv){stp(false);}getNs(value);var old = false;for(var i=0; i<ns.length; i++){for(var j=0; j<ons.length; j++){if(ons[j]==ns[i]){old = true;cs[i] = ocs[j];break;}}if(!old)cs[i] = ns[i].appearance.material.emissiveColor;}tmr.enabled = true;actv = true;st = -1;tic=-1;}function inT(v){tic=v;}function stp(stt){if(stt){tmr.enabled = false;actv = false;ons.length = 0;ocs.length = 0;}else{ons = ns;ocs = cs;}for(var i=0; i<ns.length; i++){ns[i].appearance.material.emissiveColor=cs[i];}ns.length = 0;cs.length = 0;}function eventsProcessed(){if(tic>0){if(st<0){st = tic;}var fr = (tic - st)/itv;if(fr>1){stp(true);}else{var c = Math.sin(times*Math.PI*fr);c = c * c;var clr = new SFColor(c, c, c);for(var i=0; i<ns.length; i++){ns[i].appearance.material.emissiveColor=clr;}}}}"}ROUTE TS.time TO SC.inT}DEF VCR__PROTO__FLASHER flasher{}').GetValue(0);
		e.RootNodes.Add(flasher);
	}
	var node = e.CreateField('MFNode');
	for(var i=0; i<aNodes.length; i++){
		try{
			node.Add(e.Nodes.Item(aNodes[i]));
		}catch(err){}
	}
	flasher.Fields.Item('node').Assign(node);
}

function SimulationAPI_set_emissiveColor(aNodes, nRed, nGreen, nBlue) {
	if(aNodes.length>0){
		var e = this.cortona.Engine;
		if(this.ATP==0){
			var nds = [];
			for(var i=0; i<aNodes.length; i++){
				try{
					var n = e.Nodes.Item(aNodes[i]);
					if(n){
						nds.push(n);
					}
				}catch(err){}
			}
			if(nds.length>0){
				for(var i=0; i<nds.length; i++){
					nds = nds.concat(_an_api._getAllChildren(nds[i]));
				}
				var c = e.CreateField('SFColor');
				var f;
				try{
					c.Red = nRed;
					c.Green = nGreen;
					c.Blue = nBlue;
				}catch(err){}
				e.AutoRefresh=0;
				for(var i=0; i<nds.length; i++){
					try{
						f=nds[i].Fields.Item('appearance');
						if(f.Value){
							f=f.Value.Fields.Item('material');
							if(f.Value){
								f.Value.Fields.Item('emissiveColor').Assign(c);
							}
						}
					}catch(err){}
				}
				e.AutoRefresh=1;
			}
		}else{
			var hscript=null;
			try{hscript=e.Nodes.Item("VCR__NODE__HLIGHTER");}catch(err){}
			if(hscript==null){
				var hscript = e.CreateNodeFromString('DEF VCR__NODE__HLIGHTER Script{eventIn MFNode hlNodes url "javascript: function hlNodes(value){var hcolor=value[0].emissiveColor;var nodes=[];for(var i=1;i<value.length;i++){nodes.push(value[i]);}for(var i=0;i<nodes.length;i++){var n=nodes[i];if(n){if(n.appearance && n.appearance.material)n.appearance.material.emissiveColor=hcolor;if(n.children){var ch = n.children;for(var j=0;j<ch.length;j++){nodes.push(ch[j]);}}}}}"}');
				e.RootNodes.Add(hscript);
			}
			var mNode = e.CreateNodeFromString("Material{emissiveColor "+nRed+" "+nGreen+" "+nBlue+"}");
			var mfNds = e.CreateField('MFNode');
			mfNds.Add(mNode);
			for(var i=0; i<aNodes.length; i++){
				try{
					mfNds.Add(e.Nodes.Item(aNodes[i]));
				}catch(err){}
			}
			hscript.Fields.Item('hlNodes').Assign(mfNds);
		}
	}
}

function SimulationAPI_checkVisibility(sNode) {
	var e = _an_api.cortona.Engine;
	var nd = e.Nodes.Item(sNode);
	var isVs = false;
	if(nd){
		var sz = nd.BboxSize;
		isVs = (sz.X>0 || sz.Y>0 || sz.Z>0);
		if(isVs){
			var parentField = nd.Fields.Item("parent");
			while(parentField && parentField.Value){
				nd=parentField.Value;
				if(nd.Fields.Item("whichChoice").Value<0){
					isVs = false;
					break;
				}
				parentField = nd.Fields.Item("parent");
			}
		}
	}
	return isVs;
}

function SimulationAPI__getAllChildren(node){
		var children = new Array();
		try{
			var childrenField = node.Fields.Item("children");
			for(var i=0; i<childrenField.Count; i++){
				var cnode = childrenField.GetValue(i);
				children.push(cnode);
				children = children.concat(_an_api._getAllChildren(cnode));
			}
		}catch(err){}
	return children;
}

function SimulationAPI_get_viewpoint_data() {
	if(this.is_loaded){
		var e = this.cortona.Engine;
		var m = e.CameraPosition;

    	var avatarSize = e.NavigationInfo.Fields.Item('avatarSize');
    	var posArray = this._createArrayFromField(m.Translation);
    	var oriArray = this._createArrayFromField(m.Rotation);
    	var minZoom;
    	var maxZoom;
    	try{
    		minZoom=this.control_script.Fields.Item('minZoom').Value;
    		maxZoom=this.control_script.Fields.Item('maxZoom').Value;
    	}catch(e){
    		minZoom=this.control_script.Nodes.Item('VCR_CONTROL_SCRIPT').Fields.Item('minZoom').Value;
    		maxZoom=this.control_script.Nodes.Item('VCR_CONTROL_SCRIPT').Fields.Item('maxZoom').Value;
    	}

		var v = new Array(avatarSize.GetValue(3),avatarSize.GetValue(4),avatarSize.GetValue(5),posArray[0],posArray[1],posArray[2],oriArray[0],oriArray[1],oriArray[2],oriArray[3],minZoom,maxZoom,0);
		return v;
	}
}

