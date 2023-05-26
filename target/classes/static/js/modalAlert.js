/*fade Alert*/
const fadeAlert = document.getElementById("fadeAlert");

/*modal Alert */
const modalAlert = document.getElementById("modalAlert");

function clickModalAlert() {
  fadeAlert.classList.toggle("active");
  modalAlert.classList.toggle("active");
}
