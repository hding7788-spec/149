// JScript File

/* This function checks if the input is between 0 and 1 */
function isValidTransparencyNum(num) {

    if (num >= 0 && num <= 1)
        return true;

    alert("Enter number between 0 and 1");
    return false;
}

/* This function checks if the input is a number or not */
function isNum(num) {
    if (num < 0 || num == "") {
        alert("Enter positive number")
        return false;
    }
    return true;
}

/* This function checks if the input is a hexadecimal number or not */
function isHex(entry) {
    var validChar = '0123456789ABCDEFX';
    var strlen = entry.length;

    if (strlen < 1) {
        alert('Enter color!');
        return false;
    }

    entry = entry.toUpperCase();
    for (i = 0; i < strlen; i++) {
        if (validChar.indexOf(entry.charAt(i)) < 0) {
            alert("Entry must be in hexadecimal format!");
            return false;
        }
    }
    return true;
}

/* This function clears the text input field from the html page */
function clearField(fieldName) {
    document.getElementById(fieldName).value = "";
}





