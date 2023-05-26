/*fade logout*/
const fadeLogout = document.getElementById("fadeLogout");

/*modalLogout */
const modalLogout = document.getElementById("modalLogout");

function clickModalLogout() {
  fadeLogout.classList.toggle("active");
  modalLogout.classList.toggle("active");
}
