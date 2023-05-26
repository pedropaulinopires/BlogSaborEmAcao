const btnSubmit = document.getElementById("btnSubmit");

const spin = document.getElementById("spin");

const form = document.getElementById("form");

const body = document.getElementsByTagName("body")[0];

function clickBtnSubmit() {
  spin.classList.add("active");
  body.style.pointerEvents = "none";
}
