document.addEventListener("DOMContentLoaded", function () {
    let findPWBtn = document.getElementById("findpw");

    const modalElementPW = document.getElementById("modalPW");
    const modalInstancePW = new bootstrap.Modal(modalElementPW);


    findPWBtn.addEventListener("click", function () {
      modalInstancePW.show();
    })
    
    document.querySelector("#findpw").addEventListener("click", function (){
      console.log("메일로 암호 보냄!");
    })
  });