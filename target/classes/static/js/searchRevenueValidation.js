const searchInputRevenue = document.getElementById("inputSearch");

const btnSearch = document.getElementById("btnSearch");

/*disable button*/
btnSearch.disabled = true;

searchInputRevenue.addEventListener("input", function () {
  if (searchInputRevenue.value.length > 0) {
    btnSearch.disabled = false;
  } else {
    btnSearch.disabled = true;
  }
});
