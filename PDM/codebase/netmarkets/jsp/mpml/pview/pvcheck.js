var _isIE       = false;
var _is64bitIE  = false;
var _isNS6      = false;

var g_downloaddir;
var g_docPluginVersion  = "10.1.00.23"; // version string of the form '1.2.3.4'

if (navigator.appVersion.indexOf("MSIE") != -1) {
    _isIE = true;
    if (navigator.appVersion.indexOf("Win64") != -1)
        _is64bitIE = true;
}

if (navigator.appVersion.indexOf("5.0") == 0)
    _isNS6 = true;

function CompareVersion(downloadVersion, installedVersion) {
    var loc1=0;
    var loc2=0;
    for (i=0; i< 4; i++) {
        var val1, val2;
        var locEnd = downloadVersion.indexOf('.',loc1);
        if (locEnd != -1)
            val1 = eval(downloadVersion.substring(loc1,locEnd));
        else
            val1 =eval(downloadVersion.substring(loc1));

        loc1 = locEnd+1;
        locEnd = installedVersion.indexOf('.',loc2);
        if (locEnd != -1)
            val2 = eval(installedVersion.substring(loc2,locEnd));
        else
            val2 = eval(installedVersion.substring(loc2));
        loc2 = locEnd+1;
        if (val1 > val2)
            return false;
        if (val1 < val2)
            return true;
    }
    return true
}

function PV_InsertCheck(downloaddir, versionstrings) {
    var _browser_type = "";
    var _browser_platform = "";
    var _docPluginURL = "";

    if (navigator.platform == "SunOS sun4u")
        _browser_platform="sun4_solaris";

    if (navigator.platform == "SunOS i86pc")
        _browser_platform="sun_solaris_x32";

    if (navigator.platform == "Win32"  || navigator.platform == "Win64") {
        if (_is64bitIE)
            _browser_platform = "x86e_win64";
        else
            _browser_platform = "i486_nt";
    }

    if (navigator.platform.substring(0,5) == "HP-UX")
        _browser_platform = "hpux11_pa32";

    if (navigator.platform.substring(0,3) == "AIX")
        _browser_platform = "ibm_rs6000";

    if (_browser_platform == "") {
/*
        if (typeof _pvliteString_Unsupported_Browser != "undefined")
        {
            _local_Unsupported_Browser = _pvliteString_Unsupported_Browser;
        }
        window.alert(_local_Unsupported_Browser +"\n" + navigator.platform);
*/
        return;
    }
    if (navigator.appName == "Netscape")
        _browser_type = "ns";
    else
        _browser_type = "ie";

    if (_browser_type == "" || _browser_platform == "") {
        _imageOnly = true;
    } else {
        var _browser = _browser_platform + "_" + _browser_type + "/";
        var vs = versionstrings;
        while (true) {
            var loc = vs.indexOf(";");
            var v = loc!=-1?vs.substring(0,loc):vs;
            if ( v.indexOf(_browser) == 0 ) {
                var versionLoc = v.lastIndexOf('/');
                g_docPluginVersion = v.substring(versionLoc+1);
                _docPluginURL = v.substring(0,versionLoc);
            }
            if ( loc == -1 )
                break;
            vs = vs.substring(loc+1);
        }
    }

    if (_browser_type == "ie") {
        if (_is64bitIE)
            document.write('<object classid="CLSID:82DBCFDB-5658-4cfb-B32B-0828247043C0" id="pvVerCtl"');
        else
            document.write('<object classid="CLSID:F694EA1F-2EC1-445D-8988-1862AD0CC4C8" id="pvVerCtl"');

        if (_docPluginURL) {
            document.write(' codebase="' + downloaddir + "/" + _docPluginURL);
            if (g_docPluginVersion)
                document.write('#version=' + g_docPluginVersion.replace(/\./g,","));
            document.write('"');
        }
        document.write("height=1 width=1");
        document.write('>\n</object>\n');
    } else if (_browser_type == "ns") {
        var L = navigator.plugins.length;
        var usePlugin = false;
        for (i=0; i< L; i++) {
            if (navigator.plugins[i].name == "Creo View Version Checker") {
                v = navigator.plugins[i].description;
                var versionLoc = v.lastIndexOf(' ');

                var instVer =  v.substring(versionLoc+1);
                usePlugin = CompareVersion(g_docPluginVersion, instVer);
                break;
            }
        }
        if (usePlugin) {
            document.write('<embed name="pvVerCtl" ');
            document.write(' id="pvVerCtl"');
            document.write(' pluginurl=' + downloaddir + "/" + _docPluginURL);
            document.write(' pluginspage="' + downloaddir + "/" + _docPluginURL + '"');
            document.write(' type="application/x-pvlite9-ver" ');
            document.write(' hidden="true"');
            document.write('>\n');

        } else {
            g_downloaddir = downloaddir;
            xpi={'XPInstall Creo View Version Checker':downloaddir + "/" + _docPluginURL};
            InstallTrigger.install(xpi,OnInstalledFinished);
        }
    }
}

function _xpiInstallCallback(url, status) {
    url = window.location;
    if (status == 0) {
        navigator.plugins.refresh(false);
        window.location.href = url;
        document.cookie="pvlite_version_checked=true;path=/";
    } else if (status == 999) {
        window.location.href = url;
        document.cookie="pvlite_reboot_needed=true;path=/";
    } else {
        if (typeof _pvliteString_Install_Failed != "undefined")
            _local_Install_Failed = _pvliteString_Install_Failed;

        msg = _local_Install_Failed +" " +status+"\n"+url;
        window.alert(msg);
    }
    alert("Please restart Firefox in order to use this plugin");
}

function OnInstalledFinished( name , result ) {
    if (result == 0) {
        PV_InsertCheck(g_downloaddir,"");
        InstallTrigger.install(_urllist, _xpiInstallCallback);
    } else {
        alert("Download failed");
    }
}

function IsPviewInstalled() {
    return document.pvVerCtl.CheckPview(g_docPluginVersion);
}
