document.addEventListener("DOMContentLoaded", function () {
  const editButton = document.querySelector(".btn-primary.me-2"); // 수정 버튼
  const quitButton = document.querySelector("#quit"); // 탈퇴 버튼
  const confirmButton = document.querySelector("#confirm"); // 확인 버튼
  const cancelButton = document.querySelector("#cancel"); // 취소 버튼
  
    const inputs = document.querySelectorAll("input, select");
    inputs.forEach((input) => input.setAttribute("disabled", true));
    confirmButton.style.display = "none";
    cancelButton.style.display = "none";
    // 수정 버튼 클릭 시 동작
  	editButton.addEventListener("click", function (event) {
    event.preventDefault(); // 기본 제출 방지

    // 입력 필드 활성화
    inputs.forEach((input) => input.removeAttribute("disabled"));

    // 버튼 전환 (수정/탈퇴 → 확인/취소)
    editButton.style.display = "none";
    quitButton.style.display = "none";
    confirmButton.style.display = "flex";
    cancelButton.style.display = "flex";
  });

  // 취소 버튼 클릭 시 원래 상태로 되돌림
  cancelButton.addEventListener("click", function () {
    // 입력 필드 다시 비활성화
    inputs.forEach((input) => input.setAttribute("disabled", true));

    // 버튼 전환 (확인/취소 → 수정/탈퇴)
    editButton.style.display = "flex";
    quitButton.style.display = "flex";
    confirmButton.style.display = "none";
    cancelButton.style.display = "none";
  });
  });
  
 