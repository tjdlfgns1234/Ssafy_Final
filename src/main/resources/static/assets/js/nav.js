document.addEventListener("DOMContentLoaded", function () {
    const loggedInUser = localStorage.getItem("loggedInUser");
    const notLoggedInMenu = document.querySelector(".not-logged-in");
    const loggedInMenu = document.querySelector(".logged-in");
    const logoutBtn = document.getElementById("logout");
  
    if (loggedInUser) {
      // 로그인 상태: 로그인 전 UI 숨기고, 로그인 후 UI 표시
      notLoggedInMenu.classList.add("hide");
      loggedInMenu.classList.remove("hide");
  
      // 로그아웃 버튼 클릭 시 로그아웃 처리
      logoutBtn.addEventListener("click", function () {
        localStorage.removeItem("loggedInUser");
        alert("로그아웃 되었습니다.");
        window.location.reload();
      });
    } else {
      // 로그아웃 상태: 로그인 후 UI 숨기고, 로그인 전 UI 표시
      notLoggedInMenu.classList.remove("hide");
      loggedInMenu.classList.add("hide");
    }
  });
  