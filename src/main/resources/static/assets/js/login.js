document.addEventListener("DOMContentLoaded", function () {
    let findIdBtn = document.getElementById("findid");
    let findPWBtn = document.getElementById("findpw");

    const modalElementID = document.getElementById("modalId");
    const modalInstanceID = new bootstrap.Modal(modalElementID);

    const modalElementPW = document.getElementById("modalPW");
    const modalInstancePW = new bootstrap.Modal(modalElementPW);

    findIdBtn.addEventListener("click", function () {
      modalInstanceID.show();
    })

    findPWBtn.addEventListener("click", function () {
      modalInstancePW.show();
    })
    
    document.querySelector("#findpw").addEventListener("click", function (){
      console.log("메일로 암호 보냄!");
    })
  });