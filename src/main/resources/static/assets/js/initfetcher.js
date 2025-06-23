const area = document.querySelector("#areaCode");
const sigungu = document.querySelector("#sigunguCode");
const contentType = document.querySelector("#contentType");
const areaCode1 = async (areaCode) => {
    const queryObj = {
        serviceKey: key_data,
        numOfRows: 120,
        pageNo: 1,
        MobileOS: "ETC",
        MobileApp: "Test",
        _type: "json",
    };
    if (areaCode) {
        queryObj.areaCode = areaCode;
    }
    try {
        const json = await getFetch("https://apis.data.go.kr/B551011/KorService1/areaCode1", queryObj);
        console.log(json);
        let info = json.response.body.items.item;
        info.forEach((item) => {
            item.key = item.code;
            item.label = item.name;
        });
    if (areaCode) {
        updateSelect(sigungu, "시군구", info);
    } else {
        updateSelect(area, "지역", info);
    }
    } catch (e) {
    console.log(e);
    }
};
const updateSelect = (select, type, data) => {
    select.innerHTML = `<option value="" selected disabled>${type} 선택</option>`;
    if (data) {
        data.forEach((element) => {
        select.innerHTML += `<option value="${element.key}">${element.label}</option>`;
        });
    }
};
area.addEventListener("change", async function () {
    await areaCode1(area.value);
});
area.addEventListener("click", async function() {
	if (!area.value) await areaCode1();
});
const getFetch = async (url, param, isXml) => {
    try {
        const queryString = new URLSearchParams(param).toString();
        const response = await fetch(url + "?" + queryString);
        let result = "";
        if (isXml) {
            result = await response.text();
        } else {
            result = await response.json();
        }
        console.log("요청 URL: " + url, param, result);
        return result;
    } catch (e) {
        console.log(e);
        throw e;
    }
};