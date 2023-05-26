function toggleFavorite(id){
    $.ajax({
        method: "GET",
        url: "http://ec2-34-238-239-157.compute-1.amazonaws.com:8080/favorito/"+id,
        contentType: "application/json; charset=utf-8",
        success: function (response) {
            document.getElementById(id).classList.toggle("active");
        },
      }).fail(function (xhr, status, errprThrown) {
        console.log("error add favorite ==>> "+xhr)
      });
    

}






