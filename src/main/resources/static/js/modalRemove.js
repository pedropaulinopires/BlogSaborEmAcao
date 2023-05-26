/*fade Remove*/
const fadeRemove = document.getElementById("fadeRemove");

/*Modal Remove */
const modalRemove = document.getElementById("modalRemove");

const idRemove = document.getElementById("idRemove");

function activeModalRemove(id) {
  idRemove.setAttribute("href", "/favorito/" + id + "/remover");
  fadeRemove.classList.toggle("active");
  modalRemove.classList.toggle("active");
}

function clickModalRemove() {
  fadeRemove.classList.toggle("active");
  modalRemove.classList.toggle("active");
}
