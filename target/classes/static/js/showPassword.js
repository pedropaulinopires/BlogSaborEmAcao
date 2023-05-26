const showPassword = document.getElementById("ishow");

const inputPassword = document.getElementById("ipassword");

function checkShowPassword() {
  if (showPassword.checked === true) {
    //showPassword
    inputPassword.type = "text";
  } else {
    //hidePassword
    inputPassword.type = "password";
  }
}
