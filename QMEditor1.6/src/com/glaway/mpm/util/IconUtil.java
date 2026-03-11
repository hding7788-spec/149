package com.glaway.mpm.util;

import com.glaway.mpm.view.NewTechnicsPart;

import javax.swing.ImageIcon;

public class IconUtil {
    public static final String EDITNUMBER = "/images/codingClassi_update.gif";
	public static final String COPY = "/images/copy_edit.gif";
	public static final String CUT = "/images/cut_edit.gif";
	public static final String PASTE = "/images/paste_edit.gif";
	public static final String DELETE = "/images/delete_edit.gif";
	public static final String DELETE_ALL = "/images/clear.gif";
	public static final String SAVEAS_PICTURE = "/images/save_as_picture.gif";
	public static final String AUTO_ALIGN_COLUMN_WIDTH = "/images/align_dialog.gif";
	public static final String SELECT_ALL = "/images/PVLite_IconSelectAll.gif";
	public static final String GENERAL = "/images/PVLite_IconSelectMode.gif";
	public static final String SELECT = "/images/palette_select.gif";
	public static final String TABLE = "/images/jsf-data-table-16.gif";
	public static final String RECTANGLE = "/images/rectangle.gif";
	public static final String FLOW_CHART = "/images/field_to_local.gif";
	public static final String IMAGE = "/images/image-16.gif";
	public static final String SPECIAL_SYMBOL = "/images/text.png";
	public static final String UNDO = "/images/undo16.gif";
	public static final String REDO = "/images/redo16.gif";
	public static final String NEW = "/images/new_template.gif";
	public static final String OPEN = "/images/open_template.gif";
	public static final String SAVE = "/images/save_template.gif";
	public static final String PRINT = "/images/print_template.gif";
	public static final String DELETE_TEMPLATE = "/images/delete_template.gif";
	public static final String SET_SAVE_PERIOD = "/images/time-span-16.png";
	public static final String PREVIEW = "/images/preview_template.gif";
	public static final String PART_EXPORT = "/images/IconTemplateDownload.gif";
	public static final String EBOM_EXPORT = "/images/sort.gif";
	public static final String TECHNICS_EXPORT = "/images/IconDownloadDocToCompressedFile.gif";
	public static final String TECHNICS_ZIP = "/images/zip.gif";
	public static final String MY_SETTING = "/images/cat_icon_connect.png";
	public static final String SORT = "/images/align_height.gif";
	public static final String RE_GENERATE_STEPNUMBER = "/images/align_v_space.gif";
	public static final String QUICK_CREATE = "/images/table.gif";
	public static final String AUTO_CREATE = "/images/partM_dependency.gif";
	public static final String SEARCH_TECHNICS = "/images/viewm.gif";
	public static final String SEARCH_PRODUCT = "/images/zoomwindow.gif";
	public static final String WORKSPACE = "/images/home.gif";
	public static final String NEW_TECHNICS = "/images/technics.gif";
	public static final String NEW_REWORKTECHNICS = "/images/rework.gif";
	public static final String NEW_TEMPTECHNICS = "/images/temp.gif";
	public static final String NEW_STEP = "/images/procedure.gif";
	public static final String IMPORT_TECHNICS_TEMPLATE = "/images/technics_workingIcon.gif";
	public static final String REFRESH_STEP_NUMBER = "/images/refreshStepNumber.gif";
	public static final String NEW_PACE = "/images/step.gif";
	public static final String EBOM_SIGN_RED = "/images/ppoint.gif";
	public static final String PART_ADD = "/images/part_openIcon.gif";
	public static final String OPEN_FILE = "/images/openFile.jpg";
	public static final String CREATE_PROCEDURE_FLOW = "/images/field_to_local.gif";
	public static final String ENTIRETYPART = "/images/domain.gif";
	public static final String COMBINATIONPART = "/images/zuhe.gif";
	public static final String NORMALPART = "/images/part.gif";
	public static final String ASSISTANTPART = "/images/fujian.gif";
	public static final String MIDDLEPART = "/images/zhongjian.gif";
	public static final String ASSEMBLETECHNICS = "/images/technics.gif";
	public static final String PARTTECHNICS = "/images/lingjian.gif";
	public static final String PROCEDURE = "/images/procedure.gif";
	public static final String TECHNICSUPDATE = "/images/workitem_needful.gif";
	public static final String PBOMUPDATE = "/images/workitem_needless.gif";
	public static final String VIEWHISTORYTECHNICS = "/images/viewm.gif";
	public static final String TECHNICSUPLOAD = "/images/partM_upArrow.gif";
	public static final String ASSEMBLETOOL = "/images/cabinet_grn_openIcon.gif";
	public static final String ASSEMBLEPIC = "/images/total.gif";
	public static final String SINGLE_LINE = "/images/single_line.gif";
	public static final String HORIZONTAL_DOUBLE__LINE = "/images/horizontal_double_line.gif";
	public static final String VERTICAL_DOUBLE__LINE = "/images/vertical_double_line.gif";
	public static final String HORIZONTAL_TREBLE_LINE = "/images/horizontal_treble_line.gif";
	public static final String VERTICAL_TREBLE_LINE = "/images/vertical_treble_line.gif";
	public static final String SINGLE_LINE_D = "/images/single_d.png";
	public static final String HORIZONTAL_DOUBLE__LINE_D = "/images/horizontal_double_d.png";
	public static final String VERTICAL_DOUBLE__LINE_D = "/images/vertical_double_d.png";
	public static final String HORIZONTAL_TREBLE_LINE_D = "/images/horizontal_treble_d.png";
	public static final String VERTICAL_TREBLE_LINE_D = "/images/vertical_treble_d.png";
	public static final String HORIZONTAL_QUINTUPLE_LINE = "/images/horizontal_quintuple.png";
	public static final String HORIZONTAL_QUINTUPLE_LINE_D = "/images/horizontal_quintuple_d.png";
	public static final String JPG = "/images/jpg.gif";
	public static final String DEFAULT = "/images/PVLite_IconSelectMode.gif";
	public static final String TOTAL_DELETE = "/images/clear.gif";
	public static final String REFRESH = "/images/refresh.png";
	public static final String TO_UP = "/images/align_v_top.gif";
	public static final String TO_BOTTOM = "/images/align_v_bottom.gif";
	public static final String TO_LEFT = "/images/align_h_left.gif";
	public static final String TO_RIGHT = "/images/align_h_right.gif";
	public static final String TO_HORIZONTAL_CENTER = "/images/align_h_centers.gif";
	public static final String TO_VERTICAL_CENTER = "/images/align_v_centers.gif";
	public static final String RESET = "/images/field_to_local.gif";
	public static final String NOTE = "/images/coding_update.gif";
	public static final String PREVIOUS_STEP = "/images/public_back.gif";
	public static final String NEXT_STEP = "/images/public_forward.gif";
	public static final String ADD_FROCK = "/images/button_add1.png";
	public static final String TECHNICS = "/images/technics.gif";
	public static final String PLANCOMPLETE = "/images/planComplete.gif";
	public static ImageIcon getImageIcon(String iconName) {
		ImageIcon returnImageIcon = new ImageIcon(
				NewTechnicsPart.class.getResource(iconName));
		return returnImageIcon;
	}

	public static void main(String[] args) throws Exception {
	}
}
