const srcollToTop = document.getElementById("scroll");

window.addEventListener("scroll", () => {
  if (window.pageYOffset > 350) {
    //show scroll top
    srcollToTop.classList.add("active");
  } else {
    //remove scroll top
    srcollToTop.classList.remove("active");
  }
});

function toTop() {
  window.scrollTo(0, 0);
}
