/*MENU MOBILE*/

/*menu button*/
const menuBtn = document.getElementById("menuBtn");

/*list principal*/
const listMobile = document.getElementById("optionMobile");

/*list expecial*/
const listExpecial = document.getElementById("expecialMenu");

/*btn expecial */
const bntExpecial = document.getElementById("btnExpecial");

/*more*/
const more = document.getElementById("more");

/*lessyI*/
const less = document.getElementById("less");

/*boolean expecial*/
var expecialActive = false;

/*boolean expecial*/
var menuOpen = false;

function menuShow() {
  if (expecialActive === true) {
    listExpecial.classList.remove("active");
    listMobile.classList.remove("extend");
    more.classList.remove("active");
    less.classList.remove("active");
    listMobile.classList.remove("active");
    bntExpecial.classList.remove("active");
    menuBtn.classList.remove("active");
    expecialActive = false;
    menuOpen = false;
  } else if (menuOpen === false) {
    listMobile.classList.add("active");
    menuBtn.classList.add("active");
    menuOpen = true;
  } else if (menuOpen === true) {
    listMobile.classList.remove("active");
    menuBtn.classList.remove("active");
    menuOpen = false;
  }
}

function menuExpecial() {
  if (expecialActive === false) {
    listExpecial.classList.add("active");
    listMobile.classList.add("extend");
    more.classList.add("active");
    less.classList.add("active");
    bntExpecial.classList.add("active");
    expecialActive = true;
  } else {
    listExpecial.classList.remove("active");
    listMobile.classList.remove("extend");
    more.classList.remove("active");
    less.classList.remove("active");
    bntExpecial.classList.remove("active");
    expecialActive = false;
  }
}

function closeAllMenu() {
  listExpecial.classList.remove("active");
  listMobile.classList.remove("extend");
  more.classList.remove("active");
  less.classList.remove("active");
  bntExpecial.classList.remove("active");
  listMobile.classList.toggle("active");
  menuBtn.classList.remove("active");
  expecialActive = false;
  menuOpen = false;
}

/*MENU DESKTOP*/

/*list expecial desktop*/
const listExpecialDesktop = document.getElementById("listExpecialDesktop");

/*More desktop icon*/
const moreDesktop = document.getElementById("moreDesktop");

/*Less desktop icon*/
const lessDesktop = document.getElementById("lessDesktop");

function showListExpecialDesktop() {
  listExpecialDesktop.classList.add("active");
  moreDesktop.classList.add("active");
  lessDesktop.classList.add("active");
}

function hideListExpecialDesktop() {
  listExpecialDesktop.classList.remove("active");
}

/*reset menu*/
function resetMenu() {
  if (window.innerWidth >= 1250) {
    if (menuOpen === true && expecialActive === true) {
      //close all
      listExpecial.classList.remove("active");
      more.classList.remove("active");
      less.classList.remove("active");
      bntExpecial.classList.remove("active");
      listMobile.classList.remove("active");
      menuBtn.classList.remove("active");
      expecialActive = false;
      menuOpen = false;
    } else if (menuOpen === true) {
      //close menu
      listMobile.classList.remove("active");
      menuBtn.classList.remove("active");
      menuOpen = false;
    }
  }
}

function closeMenuMobile() {
  if (menuOpen === true && expecialActive === true) {
    //close all
    listExpecial.classList.remove("active");
    more.classList.remove("active");
    less.classList.remove("active");
    bntExpecial.classList.remove("active");
    listMobile.classList.remove("active");
    menuBtn.classList.remove("active");
    expecialActive = false;
    menuOpen = false;
  } else if (menuOpen === true) {
    //close menu
    listMobile.classList.remove("active");
    menuBtn.classList.remove("active");
    menuOpen = false;
  }
}
