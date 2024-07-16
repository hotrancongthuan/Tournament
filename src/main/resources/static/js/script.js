if (document.getElementById("select_tournament") !== null) {
    document.getElementById('select_tournament').addEventListener('change', function() {
        let year = this.value;
        console.log("Giá trị đã chọn: " + year);
        if (year) {
            var newUrl = window.location.protocol + '//' + window.location.host + '/' + year;
            window.location.href = newUrl;
        }
    });
}
if (
  document.getElementById("confirmTeamInfo__round") !== null ||
  document.getElementById("index__round") !== null ||
  document.getElementById("scheduleMatch__round") !== null
) {
  const prevBtn = document.querySelector(".aside__round--btnPrev"),
    nextBtn = document.querySelector(".aside__round--btnNext"),
    listRound = document.querySelectorAll(".aside__round ul li");
  let index = 1;

  if (listRound.length > 1) {
    prevBtn.addEventListener("click", () => {
      if (index > 1) {
        let round = document.querySelector("li.round--" + index);
        round.classList.remove("round--show");
        index--;
        let prevRound = document.querySelector("li.round--" + index);
        prevRound.classList.add("round--show");

        let prevInput = document.querySelector("input.round--" + index);
        document.getElementById(prevInput.id).checked = true;
      }
    });

    nextBtn.addEventListener("click", () => {
      if (index < listRound.length) {
        let round = document.querySelector("li.round--" + index);
        round.classList.remove("round--show");
        index++;
        let nextRound = document.querySelector("li.round--" + index);
        nextRound.classList.add("round--show");

        let nextInput = document.querySelector("input.round--" + index);
        document.getElementById(nextInput.id).checked = true;
      }
    });
  }
}

if (document.getElementById("mainpage_tour") !== null) {
    let currentGroupIndex = 1;
    const groups = document.querySelectorAll('.groupInfo');
    const totalGroups = groups.length;

    function updateGroupDisplay() {
        groups.forEach((group, index) => {
            if (index + 1 === currentGroupIndex) {
                group.classList.add("show");
            } else {
                group.classList.remove("show");
            }
        });
    }

    document.getElementById("btn-nextGroup").addEventListener("click", function() {
        if (currentGroupIndex === totalGroups) {
            currentGroupIndex = 1;
        } else {
            currentGroupIndex++;
        }
        updateGroupDisplay();
    });

    document.getElementById("btn-prevGroup").addEventListener("click", function() {
        if (currentGroupIndex === 1) {
            currentGroupIndex = totalGroups;
        } else {
            currentGroupIndex--;
        }
        updateGroupDisplay();
    });
}



function addPlayer(matchID,team1ID,team1Title_,team2ID,team2Title_,teamIndex,index) {

    const playerScore__team = document.querySelector(`#playerScore__team${teamIndex} ul`)

    const csrfToken = document
        .querySelector('meta[name="_csrf"]')
        .getAttribute("content");
      const csrfHeaderName = document
        .querySelector('meta[name="_csrf_header"]')
        .getAttribute("content");

      fetch("/api/getPlayerAttendance", {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
          [csrfHeaderName]: csrfToken,
        },
        body: JSON.stringify({ matchID : matchID,team1ID : team1ID,team2ID : team2ID}),
      })
        .then((response) => {
          if (!response.ok) {
            window.location.href = "/index";
            return null;
          }
          return response.json();
        })
        .then((data) => {
          if (data) {
            let optionsTeam1 = data.playersTeam1.map(player => `<option value="${player.id}">${player.name} - ${player.studentID}</option>`).join('');
            let optionsTeam2 = data.playersTeam2.map(player => `<option value="${player.id}">${player.name} - ${player.studentID}</option>`).join('');
            let selectHtml = `
              <li>
                <select id="playerScoreTeam${teamIndex}_${index}" name="playerScoreTeam${teamIndex}_${index}">
                  <option value="">Cầu thủ ghi bàn</option>
                  <optgroup label="Đội ${team1Title_}">
                    ${optionsTeam1}
                  </optgroup>
                  <optgroup label="Đội ${team2Title_} (Phản lưới nhà)">
                    ${optionsTeam2}
                  </optgroup>
                </select>
                <select id="playerAssistTeam${teamIndex}_${index}" name="playerAssistTeam${teamIndex}_${index}">
                  <option value="">Kiến tạo</option>
                  ${optionsTeam1}
                </select>
                <input type="number" id="minutesScoreTeam${teamIndex}_${index}" name="minutesScoreTeam${teamIndex}_${index}" value="" />
              </li>
            `;
            playerScore__team.innerHTML += selectHtml;
          }
        })
        .catch((error) => {
          console.error("Error:", error);
        });
}

function addPenalty(matchID,team1ID,team1Title_,team2ID,team2Title_,teamIndex) {
    const csrfToken = document
        .querySelector('meta[name="_csrf"]')
        .getAttribute("content");
    const csrfHeaderName = document
        .querySelector('meta[name="_csrf_header"]')
        .getAttribute("content");
    const penalty__team = document.querySelector(`#playerPenalty__team${teamIndex} ul`);

  fetch("/api/getPlayerAttendance", {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      [csrfHeaderName]: csrfToken,
    },
        body: JSON.stringify({ matchID : matchID,team1ID : team1ID,team2ID : team2ID}),
    })
    .then((response) => {
      if (!response.ok) {
        window.location.href = "/index";
        return null;
      }
      return response.json();
    })
    .then((data) => {
      if (data) {
        let html = data.playersTeam1.map((player, index) => `
            <li>
              <input type="checkbox" id="playersPenaltyTeam${teamIndex}_${index}" value="${player.id}" />
              <label class="namePlayer" for="playersPenaltyTeam${teamIndex}_${index}">${player.name} - ${player.studentID}</label>
              <span>
                    <input
                        name="resultsPenaltyTeam${teamIndex}_${index}"
                        type="radio"
                         id="penaltyWin_${player.id}"
                         value="1"
                    />
                    <label for="penaltyWin_${player.id}">Thành công</label>
              </span>
              <span>
                    <input
                        name="resultsPenaltyTeam${teamIndex}_${index}"
                        type="radio"
                        id="penaltyFail_${player.id}"
                        value="0"
                    />
                    <label for="penaltyFail_${player.id}">Thất bại</label>
              </span>
            </li>
        `).join('');
        console.log()
        penalty__team.innerHTML += html;
      }
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}

function addCard(matchID,team1ID,team1Title_,team2ID,team2Title_,teamIndex,index) {
    const csrfToken = document
        .querySelector('meta[name="_csrf"]')
        .getAttribute("content");
    const csrfHeaderName = document
        .querySelector('meta[name="_csrf_header"]')
        .getAttribute("content");
    const playerCard__team = document.querySelector(`#playerCard__team${teamIndex} ul`);

  fetch("/api/getPlayerAttendance", {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      [csrfHeaderName]: csrfToken,
    },
        body: JSON.stringify({ matchID : matchID,team1ID : team1ID,team2ID : team2ID}),
    })
    .then((response) => {
      if (!response.ok) {
        window.location.href = "/index";
        return null;
      }
      return response.json();
    })
    .then((data) => {
      if (data) {
        let optionsTeam1 = data.playersTeam1.map(player => `<option value="${player.id}">${player.name} - ${player.studentID}</option>`).join('');
        let optionsTeam2 = data.playersTeam2.map(player => `<option value="${player.id}">${player.name} - ${player.studentID}</option>`).join('');
        let selectHtml = `
          <li>
            <select id="playersCardTeam${teamIndex}_${index}" name="playersCardTeam${teamIndex}_${index}">
              <option value="">Cầu thủ phạm lỗi</option>
              ${optionsTeam1}
            </select>
            <select id="typeCardTeam${teamIndex}_${index}" name="typeCardTeam${teamIndex}_${index}">
              <option value="">Loại thẻ phạt</option>
              <option value="redCard">Thẻ đỏ</option>
              <option value="yellowCard">Thẻ vàng</option>
            </select>
            <input id="minutesCardTeam${teamIndex}_${index}" type="number" name="minutesCardTeam1" value="" />
          </li>
        `;
        playerCard__team.innerHTML += selectHtml;
      }
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}

if (document.getElementById("numberScore__team1") !== null) {
    const scoreBtnTeam1 = document.getElementById("numberScore__team1"),
    scoreBtnTeam2 = document.getElementById("numberScore__team2"),
    cardBtnTeam1 = document.getElementById("numberCard__team1"),
    cardBtnTeam2 = document.getElementById("numberCard__team2");

    const matchID = document.getElementById("getMatchID");
    const team1ID = document.getElementById("getTeam1ID");
    const team2ID = document.getElementById("getTeam2ID");

    let team1Data = document.querySelector('.lineup__team--title.t1');
    let team2Data = document.querySelector('.lineup__team--title.t2');
    let team1Title = team1Data.getAttribute('data-team');
    let team2Title = team2Data.getAttribute('data-team');

  scoreBtnTeam1.addEventListener(
    "click",
    function () {
      const playerScore__team1 = document.querySelector("#playerScore__team1 ul"),
      inputScore__team1 = document.getElementById("inputNumberScore__team1");
      playerScore__team1.innerHTML = "";
      msg_error = document.querySelector(".messager__error");
      if (inputScore__team1.value === "") {
        msg_error.classList.add("score");
        msg_error.innerHTML = "Vui lòng nhập số bàn thắng đội ghi được";
      } else {
        if (msg_error.classList.contains("score")) {
          msg_error.classList.remove("score");
          msg_error.innerHTML = "";
        }
        let number_ = inputScore__team1.value;
        for (let i = 0; i < number_; i++) {
          addPlayer(matchID.value,team1ID.value,team1Title,team2ID.value,team2Title,"1",i);
        }
      }
    },
    false
  );

  scoreBtnTeam2.addEventListener(
    "click",
    function () {
      const playerScore__team2 = document.querySelector("#playerScore__team2 ul"),
        inputScore__team2 = document.getElementById("inputNumberScore__team2");
      playerScore__team2.innerHTML = "";
      msg_error = document.querySelector(
        ".result__info.team2 .messager__error"
      );
      if (inputScore__team2.value === "") {
        msg_error.classList.add("score");
      } else {
        if (msg_error.classList.contains("score")) {
          msg_error.classList.remove("score");
        }
        let number_ = inputScore__team2.value;
        for (let i = 0; i < number_; i++) {
          addPlayer(matchID.value,team2ID.value,team2Title,team1ID.value,team1Title,"2",i);
        }
      }
    },
    false
  );

  cardBtnTeam1.addEventListener(
    "click",
    function () {
      const playerCard__team1 = document.querySelector("#playerCard__team1 ul"),
        inputCard__team1 = document.getElementById("inputNumberCard__team1");
      msg_error = document.querySelectorAll(
        ".result__info.team1 .messager__error"
      );
      if (inputCard__team1.value === "") {
        msg_error[1].classList.add("cardd");
      } else {
        if (msg_error[1].classList.contains("cardd")) {
          msg_error[1].classList.remove("cardd");
        }

        let number_ = inputCard__team1.value;
        playerCard__team1.innerHTML = "";
        for (let i = 0; i < number_; i++) {
            addCard(matchID.value,team1ID.value,team1Title,team2ID.value,team2Title,"1",i);
        }
      }
    },
    false
  );

  cardBtnTeam2.addEventListener(
    "click",
    function () {
      const playerCard__team2 = document.querySelector("#playerCard__team2 ul"),
        inputCard__team1 = document.getElementById("inputNumberCard__team2");
      msg_error = document.querySelectorAll(
        ".result__info.team2 .messager__error"
      );
      if (inputCard__team1.value === "") {
        msg_error[1].classList.add("cardd");
      } else {
        if (msg_error[1].classList.contains("cardd")) {
          msg_error[1].classList.remove("cardd");
        }

        let number_ = inputCard__team1.value;
        playerCard__team2.innerHTML = "";
        for (let i = 0; i < number_; i++) {
          addCard(matchID.value,team2ID.value,team2Title,team1ID.value,team1Title,"2",i);
        }
      }
    },
    false
  );

  const checkPenaltyTeam1 = document.getElementById("inputCheckPenalty__team1");
  checkPenaltyTeam1.addEventListener("change", function() {
    const penalty__team1 = document.querySelector("#playerPenalty__team1 ul");
      if (this.checked) {
          penalty__team1.innerHTML = "";
          addPenalty(matchID.value,team1ID.value,team1Title,team2ID.value,team2Title,"1");
      } else {
          penalty__team1.innerHTML = "";
      }
  });

    const checkPenaltyTeam2 = document.getElementById("inputCheckPenalty__team2");
    checkPenaltyTeam2.addEventListener("change", function() {
        const penalty__team2 = document.querySelector("#playerPenalty__team2 ul");
        if (this.checked) {
            penalty__team2.innerHTML = "";
            addPenalty(matchID.value,team2ID.value,team2Title,team1ID.value,team1Title,"2");
        } else {
            penalty__team2.innerHTML = "";
        }
    });

  function updateMatch(){

    const inputScore__team1 = document.getElementById("inputNumberScore__team1");
    const inputScore__team2 = document.getElementById("inputNumberScore__team2");
    const inputCard__team1 = document.getElementById("inputNumberCard__team1");
    const inputCard__team2 = document.getElementById("inputNumberCard__team2");
    const checkPenaltyTeam1 = document.getElementById("inputCheckPenalty__team1");
    const checkPenaltyTeam2 = document.getElementById("inputCheckPenalty__team2");


      if(inputScore__team1.value !== "0" && inputScore__team2 !== "0"){
          if(inputScore__team1.value == ''){
            const error = document.querySelector(".result__info.team1 .messager__error");
            error.classList.add("score");
            error.innerHTML = "Nhập số bàn thắng của cả 2 đội";
              inputScore__team1.focus();
              return;
          }else{
            const error = document.querySelector(".result__info.team1 .messager__error");
            error.classList.remove("score");
          }

          if(inputScore__team2.value == ''){
              inputScore__team2.focus();
              const error = document.querySelector(".result__info.team2 .messager__error");
              error.classList.add("score");
              error.innerHTML = "Nhập số bàn thắng của cả 2 đội";
              return;
          }else{
               const error = document.querySelector(".result__info.team2 .messager__error");
               error.classList.remove("score");
            }
      }

    let countScoreTeam1 = 0;
    let countScoreTeam2 = 0;

    const playerPenalty__team1 = document.querySelectorAll("#playerPenalty__team1 ul li");
    const playerPenalty__team2 = document.querySelectorAll("#playerPenalty__team2 ul li");

    const matchID = document.getElementById("getMatchID").value;

    let playersScoreTeam1 = [];
    let playersCardTeam1 = [];
    let playersPenaltyTeam1 = [];

    let playersScoreTeam2 = [];
    let playersCardTeam2 = [];
    let playersPenaltyTeam2 = [];

    for(let i = 0; i < inputScore__team1.value; i++){
        if(document.getElementById("playerScoreTeam1_"+i) && document.getElementById("minutesScoreTeam1_"+i)){
            const playerScore = document.getElementById("playerScoreTeam1_"+i).value;
            const minutesScore = document.getElementById("minutesScoreTeam1_"+i).value;
            if(playerScore !== '' && minutesScore !== ""){
                countScoreTeam1 += 1;
                const playerAssist = document.getElementById("playerAssistTeam1_"+i).value;
                playersScoreTeam1.push({playerScore:playerScore,playerAssist:playerAssist,minutesScore:minutesScore});
            }else{
                  const error1 = document.querySelector(".result__info.team1 .messager__error");
                  error1.classList.add("score");
                  error1.innerHTML = "Chọn cầu thủ và số phút ghi bàn";
                  return;
            }
            const error1 = document.querySelector(".result__info.team1 .messager__error");
            error1.classList.remove("score");
        }else{
            const error1 = document.querySelector(".result__info.team1 .messager__error");
              error1.classList.add("score");
              error1.innerHTML = "Chọn cầu thủ ghi bàn ứng với mỗi bàn thắng";
              return;
        }
    }

    for(let i = 0; i < inputScore__team2.value; i++){
        if(document.getElementById("playerScoreTeam2_"+i) && document.getElementById("minutesScoreTeam2_"+i)){
            let playerScore = document.getElementById("playerScoreTeam2_"+i).value;
            let minutesScore = document.getElementById("minutesScoreTeam2_"+i).value;
            if(playerScore !== '' && minutesScore !== ""){
                countScoreTeam2 += 1;
                let playerAssist = document.getElementById("playerAssistTeam2_"+i).value;
                playersScoreTeam2.push({playerScore:playerScore,playerAssist:playerAssist,minutesScore:minutesScore});
            }else{
                  const error2 = document.querySelector(".result__info.team2 .messager__error");
                  error2.classList.add("score");
                  error2.innerHTML = "Chọn cầu thủ và số phút ghi bàn";
                  return;
            }
            const error2 = document.querySelector(".result__info.team2 .messager__error");
            error2.classList.remove("score");
        }else{
               const error2 = document.querySelector(".result__info.team2 .messager__error");
               error2.classList.add("score");
               error2.innerHTML = "Chọn cầu thủ ghi bàn ứng với mỗi bàn thắng";
               return;
         }
    }

    if(parseInt(inputScore__team1.value) !== countScoreTeam1){
          const error1 = document.querySelector(".result__info.team1 .messager__error");
          error1.classList.add("score");
          error1.innerHTML = "Chọn cầu thủ ghi bàn ứng với mỗi bàn thắng";
          return;
    }else{
        const error1 = document.querySelector(".result__info.team1 .messager__error");
          error1.classList.remove("score");
    }

    if(parseInt(inputScore__team2.value) !== countScoreTeam2){
      const error2 = document.querySelector(".result__info.team2 .messager__error");
      error2.classList.add("score");
      error2.innerHTML = "Chọn cầu thủ ghi bàn ứng với mỗi bàn thắng";
          return;
    }else{
       const error2 = document.querySelector(".result__info.team2 .messager__error");
       error2.classList.remove("score");
    }

    for(let i = 0; i < inputCard__team1.value; i++){
        const playerCard = document.getElementById("playersCardTeam1_"+i).value;
        const typeCard = document.getElementById("typeCardTeam1_"+i).value;
        const minutesCard = document.getElementById("minutesCardTeam1_"+i).value;
        if(playerCard !== '' && typeCard !== '' && minutesCard !== ''){
            playersCardTeam1.push({playerCard:playerCard,typeCard:typeCard,minutesCard:minutesCard});
            const error1 = document.querySelectorAll(".result__info.team1 .messager__error");
            error1[1].classList.remove("cardd");
        }else{
          const error1 = document.querySelectorAll(".result__info.team1 .messager__error");
          error1[1].classList.add("cardd");
          error1[1].innerHTML = "Nếu đội có thẻ phạt nhập đầy đủ thông tin";
          return;
        }
    }

    for(let i = 0; i < inputCard__team2.value; i++){
        const playerCard = document.getElementById("playersCardTeam2_"+i).value;
        const typeCard = document.getElementById("typeCardTeam2_"+i).value;
        const minutesCard = document.getElementById("minutesCardTeam2_"+i).value;
        if(playerCard !== '' && typeCard !== '' && minutesCard !== ''){
            playersCardTeam2.push({playerCard:playerCard,typeCard:typeCard,minutesCard:minutesCard});
            const error2 = document.querySelectorAll(".result__info.team2 .messager__error");
            error2[1].classList.remove("cardd");
        }else{
          const error2 = document.querySelectorAll(".result__info.team2 .messager__error");
          error2[1].classList.add("cardd");
          error2[1].innerHTML = "Nếu đội có thẻ phạt nhập đầy đủ thông tin nếu có không để trống";
          return;
        }
    }

    let countPenaltyTeam1 = 0;
    let countPenaltyTeam2 = 0;

    let resultPenaltyTeam1 = 0;
    let resultPenaltyTeam2 = 0;

    for (let i = 0; i < playerPenalty__team1.length; i++) {
        if(playerPenalty__team1[i].querySelector("input[type='checkbox']:checked")){
            if(playerPenalty__team1[i].querySelector("input[type='radio']:checked")){
                const playerPenalty = playerPenalty__team1[i].querySelector("input[type='checkbox']:checked").value;
                const resultPenalty = playerPenalty__team1[i].querySelector("input[type='radio']:checked").value;
                playersPenaltyTeam1.push({playerPenalty:playerPenalty,resultPenalty:resultPenalty});
                countPenaltyTeam1 += 1;
                if(resultPenalty === '1'){
                    resultPenaltyTeam1 += 1;
                }
            }else{
                const error = document.querySelector(".result__info.team1 .penalty__error");
                error.classList.add("penalty");
                error.innerHTML = "Chọn kết quả sút penalty đầy đủ";
                return;
            }
        }
    }

    for (let i = 0; i < playerPenalty__team2.length; i++) {
        if(playerPenalty__team2[i].querySelector("input[type='checkbox']:checked")){
            if(playerPenalty__team2[i].querySelector("input[type='radio']:checked")){
                const resultPenalty = playerPenalty__team2[i].querySelector("input[type='radio']:checked").value;
                const playerPenalty = playerPenalty__team2[i].querySelector("input[type='checkbox']:checked").value;
                playersPenaltyTeam2.push({playerPenalty:playerPenalty,resultPenalty:resultPenalty});
                countPenaltyTeam2 += 1;
                if(resultPenalty === '1'){
                    resultPenaltyTeam2 += 1;
                }
            }else{
                const error = document.querySelector(".result__info.team2 .penalty__error");
                error.classList.add("penalty");
                error.innerHTML = "Chọn kết quả sút penalty đầy đủ";
                return;
            }
        }
    }


    if(checkPenaltyTeam1.checked && !checkPenaltyTeam2.checked){
        const error = document.querySelector(".result__info.team1 .penalty__error");
        error.classList.add("penalty");
        error.innerHTML = "Nếu đá Penalty phải cập nhật cả 2 đội";
        const error1 = document.querySelector(".result__info.team2 .penalty__error");
        error1.classList.add("penalty");
        error1.innerHTML = "Nếu đá Penalty phải cập nhật cả 2 đội";
        return;
    }

    if(!checkPenaltyTeam1.checked && checkPenaltyTeam2.checked){
        const error = document.querySelector(".result__info.team1 .penalty__error");
        error.classList.add("penalty");
        error.innerHTML = "Nếu đá Penalty phải cập nhật cả 2 đội";
        const error1 = document.querySelector(".result__info.team2 .penalty__error");
        error1.classList.add("penalty");
        error1.innerHTML = "Nếu đá Penalty phải cập nhật cả 2 đội";
        return;
    }

    if(countPenaltyTeam1 < 5 && checkPenaltyTeam1.checked){
          const error = document.querySelector(".result__info.team1 .penalty__error");
          error.classList.add("penalty");
          error.innerHTML = "Penalty tối thiểu 5 lượt đá";
          return;
    }else{
        const error = document.querySelector(".result__info.team1 .penalty__error");
        error.classList.remove("penalty");
    }

    if(countPenaltyTeam2 < 5 && checkPenaltyTeam2.checked){
          const error = document.querySelector(".result__info.team2 .penalty__error");
          error.classList.add("penalty");
          error.innerHTML = "Penalty tối thiểu 5 lượt đá";
          return;
    }else{
        const error = document.querySelector(".result__info.team2 .penalty__error");
        error.classList.remove("penalty");
    }
    if(checkPenaltyTeam1.checked && checkPenaltyTeam2.checked){
        if(countPenaltyTeam2 !== countPenaltyTeam1){
             const error1 = document.querySelector(".result__info.team1 .penalty__error");
             const error2 = document.querySelector(".result__info.team2 .penalty__error");
             error1.classList.add("penalty");
             error2.classList.add("penalty");
             error1.innerHTML = "Penalty phải có cùng số lượt đá";
             error2.innerHTML = "Penalty phải có cùng số lượt đá";
             return;
           }else{
            const error1 = document.querySelector(".result__info.team1 .penalty__error");
            const error2 = document.querySelector(".result__info.team2 .penalty__error");
            error1.classList.remove("penalty");
            error2.classList.remove("penalty");
           }

        if(resultPenaltyTeam1 === resultPenaltyTeam2){
          const error1 = document.querySelector(".result__info.team1 .penalty__error");
          const error2 = document.querySelector(".result__info.team2 .penalty__error");
          error1.classList.add("penalty");
          error2.classList.add("penalty");
          error1.innerHTML = "Penalty không được hòa";
          error2.innerHTML = "Penalty không được hòa";
          return;
        }else{
         const error1 = document.querySelector(".result__info.team1 .penalty__error");
         const error2 = document.querySelector(".result__info.team2 .penalty__error");
         error1.classList.remove("penalty");
         error2.classList.remove("penalty");
        }
    }


    data = {matchID:matchID, scoreTeam1:playersScoreTeam1, scoreTeam2:playersScoreTeam2, cardTeam1:playersCardTeam1, cardTeam2:playersCardTeam2, penaltyTeam1:playersPenaltyTeam1, penaltyTeam2:playersPenaltyTeam2};

    const csrfToken = document
        .querySelector('meta[name="_csrf"]')
        .getAttribute("content");
    const csrfHeaderName = document
        .querySelector('meta[name="_csrf_header"]')
        .getAttribute("content");

    fetch("/api/updateMatch", {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
          [csrfHeaderName]: csrfToken,
        },
            body: JSON.stringify(data),
        })
        .then((response) => {
          if (!response.ok) {
//            window.location.href = "/index";
            return null;
          }
        })
        .then((data) => {
            window.location.reload();
          })
        .catch((error) => {
          console.error("Error:", error);
        });

//    console.log(data);
////    console.log(playersScoreTeam1);
////    console.log(playersScoreTeam2);
////    console.log(playersCardTeam1);
////    console.log(playersCardTeam2);
////    console.log(playersPenaltyTeam1);
////    console.log(playersPenaltyTeam2);
  }
}

if (document.getElementById("btn__student") !== null) {
  const fileBtnStudent = document.getElementById("input__student"),
    btnStudent = document.getElementById("btn__student"),
    textStudent = document.getElementById("text__student");

  btnStudent.addEventListener("click", function () {
    fileBtnStudent.click();
  });

  fileBtnStudent.addEventListener("change", function () {
    if (fileBtnStudent.value) {
      textStudent.innerHTML = fileBtnStudent.value.match(
        /[\/\\]([\w\d\s\.\-\(\)]+)$/
      )[1];
    } else {
      textStudent.innerHTML = "Chọn file danh sách sinh viên";
    }
  });

  const fileBtnPlanYear = document.getElementById("input__planYear"),
    btnPlanYear = document.getElementById("btn__planYear"),
    textPlanYear = document.getElementById("text__planYear");

  btnPlanYear.addEventListener("click", function () {
    fileBtnPlanYear.click();
  });

  fileBtnPlanYear.addEventListener("change", function () {
    if (fileBtnPlanYear.value) {
      textPlanYear.innerHTML = fileBtnPlanYear.value.match(
        /[\/\\]([\w\d\s\.\-\(\)]+)$/
      )[1];
    } else {
      textPlanYear.innerHTML = "Chọn file danh sách ngày nghỉ";
    }
  });

  const fileBtnWeek = document.getElementById("input__week"),
    btnWeek = document.getElementById("btn__week"),
    textWeek = document.getElementById("text__week");

  btnWeek.addEventListener("click", function () {
    fileBtnWeek.click();
  });

  fileBtnWeek.addEventListener("change", function () {
    if (fileBtnWeek.value) {
      textWeek.innerHTML = fileBtnWeek.value.match(
        /[\/\\]([\w\d\s\.\-\(\)]+)$/
      )[1];
    } else {
      textWeek.innerHTML = "Chọn file danh sách tuần học";
    }
  });

  const fileBtnScheduleSubject = document.getElementById(
      "input__scheduleSubject"
    ),
    btnScheduleSubject = document.getElementById("btn__scheduleSubject"),
    textScheduleSubject = document.getElementById("text__scheduleSubject");

  btnScheduleSubject.addEventListener("click", function () {
    fileBtnScheduleSubject.click();
  });

  fileBtnScheduleSubject.addEventListener("change", function () {
    if (fileBtnScheduleSubject.value) {
      textScheduleSubject.innerHTML = fileBtnScheduleSubject.value.match(
        /[\/\\]([\w\d\s\.\-\(\)]+)$/
      )[1];
    } else {
      textScheduleSubject.innerHTML = "Chọn file lịch học";
    }
  });
}

// trang dữ liệu
if (document.getElementById("dataStudent_option__takeIn") !== null) {
  const UrlParams = new URLSearchParams(window.location.search);

  fetch("/api/getStudentSearch", {
    method: "GET",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => {
      if (response.status === 401 || response.status === 403) {
        console.log("Session timeout. Redirecting to login page...");
        window.location.href = "/index";
        return;
      }
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.json();
    })
    .then((data) => {
      addOption(data.takeIns, "dataStudent_option__takeIn");
      addOption(data.class_, "dataStudent_option__class");
      dataStudentLoadTakeInSearch();
      dataStudentLoadClassSearch();
    })
    .catch((error) => {
      console.error("There was an error!", error);
    });

  function addOption(array, selectID) {
    const select = document.getElementById(selectID);
    for (let i = 0; i < array.length; i++) {
      let option = array[i];
      let element = document.createElement("option");
      element.textContent = option;
      element.value = option;
      select.appendChild(element);
    }
  }

  function getClassByTakeIn(select) {
    const csrfToken = document
      .querySelector('meta[name="_csrf"]')
      .getAttribute("content");
    const csrfHeaderName = document
      .querySelector('meta[name="_csrf_header"]')
      .getAttribute("content");

    let data = { data: select.value };

    fetch("/api/getClassListByTakeIn", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
        [csrfHeaderName]: csrfToken,
      },
      body: JSON.stringify(data),
    })
      .then((response) => {
        if (response.status === 401 || response.status === 403) {
          console.log("Session timeout. Redirecting to login page...");
          window.location.href = "/index";
          return;
        }
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        const selectClass = document.getElementById(
          "dataStudent_option__class"
        );
        selectClass.innerHTML = '<option value="">--</option>';
        addOption(data.class_, "dataStudent_option__class");
        dataStudentLoadClassSearch();
      })
      .catch((error) => console.error("Error:", error));
  }

  function prevPage() {
    const page = document.getElementById("dataStudent__page");
    if (!isNaN(page.value)) {
      let page_index = page.value;
      if (parseInt(page.value) > 1) {
        page.value = parseInt(page.value) - 1;
      }
      const form__dataStudent = document.getElementById(
        "formSearch__dataStudent"
      );
      form__dataStudent.submit();
    }
  }

  function nextPage() {
    const page = document.getElementById("dataStudent__page");
    if (!isNaN(page.value)) {
      let page_index = page.value;
      page.value = parseInt(page.value) + 1;
      const form__dataStudent = document.getElementById(
        "formSearch__dataStudent"
      );
      form__dataStudent.submit();
    }
  }

  if (UrlParams.has("page")) {
    const dataStudentPage = document.getElementById("dataStudent__page");
    if (UrlParams.get("page") !== null)
      dataStudentPage.value = UrlParams.get("page");
  }

  if (UrlParams.has("studentID")) {
    const dataStudentID = document.getElementById("dataStudent__ID");
    if (UrlParams.get("studentID") !== null) {
      dataStudentID.value = UrlParams.get("studentID");
    }
  }

  function dataStudentLoadTakeInSearch() {
    if (UrlParams.has("takeIn")) {
      const dataStudentTakeIn = document.getElementById(
        "dataStudent_option__takeIn"
      );
      if (UrlParams.get("takeIn") !== null) {
        dataStudentTakeIn.value = UrlParams.get("takeIn");
        getClassByTakeIn(dataStudentTakeIn);
      }
    }
  }

  function dataStudentLoadClassSearch() {
    if (UrlParams.has("class")) {
      const dataStudentClass = document.getElementById(
        "dataStudent_option__class"
      );
      if (UrlParams.get("class") !== null)
        dataStudentClass.value = UrlParams.get("class");
    }
  }
}

if (document.getElementById("formSearch__dataSchedule") !== null) {
  const UrlParams = new URLSearchParams(window.location.search);

  function prevPage() {
    const page = document.getElementById("dataSchedule__page");
    if (!isNaN(page.value)) {
      let page_index = page.value;
      if (parseInt(page.value) > 1) {
        page.value = parseInt(page.value) - 1;
      }
      const form__dataSchedule = document.getElementById(
        "formSearch__dataSchedule"
      );
      form__dataSchedule.submit();
    }
  }

  function nextPage() {
    const page = document.getElementById("dataSchedule__page");
    if (!isNaN(page.value)) {
      let page_index = page.value;
      page.value = parseInt(page.value) + 1;
      const form__dataSchedule = document.getElementById(
        "formSearch__dataSchedule"
      );
      form__dataSchedule.submit();
    }
  }

  if (UrlParams.has("page")) {
    const dataSchedulePage = document.getElementById("dataSchedule__page");
    if (UrlParams.get("page") !== null)
      dataSchedulePage.value = UrlParams.get("page");
  }

  if (UrlParams.has("subject")) {
    const dataScheduleName = document.getElementById("dataSchedule__subject");
    if (UrlParams.get("subject") !== null) {
      dataScheduleName.value = UrlParams.get("subject");
    }
  }

  if (UrlParams.has("shift")) {
    const dataScheduleShift = document.getElementById("dataSchedule__shift");
    if (UrlParams.get("shift") !== null) {
      dataScheduleShift.value = UrlParams.get("shift");
    }
  }

  if (UrlParams.has("weekday")) {
    const dataScheduleWeekDay = document.getElementById(
      "dataSchedule__weekday"
    );
    if (UrlParams.get("weekday") !== null)
      dataScheduleWeekDay.value = UrlParams.get("weekday");
  }

  if (UrlParams.has("semester")) {
    const dataScheduleWeekDay = document.getElementById(
      "dataSchedule__semester"
    );
    if (UrlParams.get("semester") !== null)
      dataScheduleWeekDay.value = UrlParams.get("semester");
  }
}

// Trang sign up team
if (document.getElementById("signupTeam__table") !== null) {
  // lấy giới hạn thành viên team từ api
  let minPlayer = 0;
  let maxPlayer = 0;
  fetch("/api/getLimitPlayerInTeam", {
    method: "GET",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => {
      if (response.status === 401 || response.status === 403) {
        console.log("Session timeout. Redirecting to login page...");
        window.location.href = "/index";
        return;
      }
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.json();
    })
    .then((data) => {
      minPlayer = data.minPlayer;
      maxPlayer = data.maxPlayer;
    })
    .catch((error) => {
      console.error("There was an error!", error);
    });

  // thêm class 2 vào modal
  function getStudentSignUp(classID, existClass2, checkClass) {
    if (checkClass === 2) {
      if (classID === document.getElementById("class1").value) {
        return;
      }
    }

    const csrfToken = document
      .querySelector('meta[name="_csrf"]')
      .getAttribute("content");
    const csrfHeaderName = document
      .querySelector('meta[name="_csrf_header"]')
      .getAttribute("content");

    fetch("/api/getStudentsSignUp", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
        [csrfHeaderName]: csrfToken,
      },
      body: JSON.stringify({ data: classID }),
    })
      .then((response) => {
        if (response.status === 401 || response.status === 403) {
          console.log("Session timeout. Redirecting to login page...");
          window.location.href = "/index";
          return;
        }

        if (response.status === 404) {
          const errorBlock = document.querySelector(".signupTeam__class2");
          const spanError = document.createElement("span");
          spanError.className = "addClass2__error";
          spanError.textContent = "*Lớp đã đăng ký";
          errorBlock.appendChild(spanError);
        }

        if (
          response.headers.get("Content-Length") === "0" ||
          response.status === 204
        ) {
          const errorBlock = document.querySelector(".signupTeam__class2");
          const spanError = document.createElement("span");
          spanError.className = "addClass2__error";
          spanError.textContent = "*Lớp không tồn tại";
          errorBlock.appendChild(spanError);
          return null;
        }

        if (response.status === 409) {
          const errorBlock = document.querySelector(".signupTeam__class2");
          const spanError = document.createElement("span");
          spanError.className = "addClass2__error";
          spanError.textContent = "*Lớp không thể tham gia giải đấu";
          errorBlock.appendChild(spanError);
          return null;
        }

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        return response.json();
      })
      .then((data) => {
        if (data !== null) {
          const studentsSignUp = document.querySelector(
            "#studentsSignUpCheckbox > div > ul"
          );
          for (const student of data) {
            const addLI = document.createElement("li");
            addLI.className = existClass2;

            const spanAddStudent = document.createElement("span");
            const checkboxAddStudent = document.createElement("input");
            checkboxAddStudent.type = "checkbox";
            checkboxAddStudent.name = "id";
            checkboxAddStudent.value = student.id;
            checkboxAddStudent.id = "checkbox_" + student.id;

            const labelNameStudent = document.createElement("label");
            labelNameStudent.htmlFor = "checkbox_" + student.id;
            labelNameStudent.textContent = student.id + " - " + student.name;

            spanAddStudent.appendChild(checkboxAddStudent);
            spanAddStudent.appendChild(labelNameStudent);

            const spanClass = document.createElement("span");
            spanClass.className = "modalAddPlayer__class";
            spanClass.textContent = "Lớp: " + student.classID;
            spanClass.setAttribute("data-class", student.classID);
            spanClass.setAttribute("data-name", student.name);

            const spanCaptain = document.createElement("span");

            const radioCaptain = document.createElement("input");
            radioCaptain.type = "radio";
            radioCaptain.name = "captain";
            radioCaptain.value = student.id;
            radioCaptain.id = "radio_" + student.id;

            const labelCaptain = document.createElement("label");
            labelCaptain.htmlFor = "radio_" + student.id;
            labelCaptain.textContent = "Đội trưởng";

            spanCaptain.appendChild(radioCaptain);
            spanCaptain.appendChild(labelCaptain);

            const numberInput = document.createElement("input");
            numberInput.name = "playerNumbers";
            numberInput.type = "number";
            numberInput.placeholder = "Số áo";
            numberInput.id = "number_" + student.id;
            numberInput.min = 1;
            numberInput.max = 99;

            addLI.appendChild(spanAddStudent);
            addLI.appendChild(spanClass);
            addLI.appendChild(spanCaptain);
            addLI.appendChild(numberInput);

            studentsSignUp.appendChild(addLI);
          }
          // gọi hàm load lại sinh viên đã chọn khi submit lỗi
          loadDataSend();
        }
      })
      .catch((error) => console.error("Error:", error));
  }

  // thêm sinh viên của lớp 2 vào modal
  function addClass2Btn() {
    const errorBlock = document.querySelector(".signupTeam__class2");
    const spanError = document.querySelector(".addClass2__error");
    if (spanError) {
      errorBlock.removeChild(spanError);
    }

    const oldInputClass2 = document.querySelectorAll(".studentClass2");
    if (oldInputClass2.length > 0) {
      oldInputClass2.forEach((class2) => {
        class2.remove();
      });
    }

    const inputClass2 = document.getElementById("addClass2");
    getStudentSignUp(inputClass2.value, "studentClass2", 2);

    const class2 = document.getElementById("class2");
    class2.value = inputClass2.value;
  }

  // thêm player vào bảng khi chọn từ modal
  function addPlayerToTable(studentID, name, className, captain, number) {
    const table = document.querySelector(".signupTeam__players table tbody");

    const tr = document.createElement("tr");

    const td1 = document.createElement("td");
    td1.innerHTML = `<a>${number}</a>
                          <input style="display:none;" type="checkbox" name="players" value="${studentID}" checked/>
                          <input style="display:none;" type="number" name="playerNumbers" value="${number}" />`;
    tr.appendChild(td1);

    const td2 = document.createElement("td");
    td2.innerHTML = `<a>${captain ? "Đội trưởng" : "Thành viên"}</a>`;
    tr.appendChild(td2);

    const td3 = document.createElement("td");
    td3.innerHTML = `<a>${name}</a>`;
    tr.appendChild(td3);

    const td4 = document.createElement("td");
    td4.innerHTML = `<a>${studentID}</a>`;
    tr.appendChild(td4);

    const td5 = document.createElement("td");
    td5.innerHTML = `<a>${className}</a>`;
    tr.appendChild(td5);

    const td6 = document.createElement("td");
    td6.innerHTML = `<a>Đăng ký</a>`;
    tr.appendChild(td6);

    const tdLast = document.createElement("td");
    tdLast.className = "signupTeam__table--button";
    tdLast.innerHTML =
      '<button type="button" class="btn btn-danger">Xóa</button>';
    tr.appendChild(tdLast);

    table.appendChild(tr); // Thêm dòng mới vào bảng
  }

  // kiểm tra lỗi modal
  function validSignupPlayer() {
    const liChecks = document.querySelectorAll(
      ".confirmTeamInfo__changePlayer ul li"
    );
    if (liChecks.length > 0) {
      let countCaptain = 0;
      let countPlayer = 0;
      let numberDuplicate = {};
      liChecks.forEach((liCheck, index) => {
        const checkbox = liCheck.querySelector("input[type='checkbox']");
        const radio = liCheck.querySelector("input[type='radio']");
        const number_ = liCheck.querySelector("input[type='number']");
        if (checkbox.checked || radio.checked || number_.value !== "") {
          if (radio.checked) {
            if (checkbox.checked && number_.value !== "") {
              liCheck.classList.remove("signup__errorInput");
              countCaptain = countCaptain + 1;
            } else {
              liCheck.classList.add("signup__errorInput");
            }
          }
          if (checkbox.checked) {
            if (number_.value === "") {
              liCheck.classList.add("signup__errorInput");
            } else {
              liCheck.classList.remove("signup__errorInput");
              countPlayer = countPlayer + 1;
              if (!numberDuplicate[number_.value]) {
                numberDuplicate[number_.value] = [];
              }
              numberDuplicate[number_.value].push(index);
            }
          } else {
            if (number_.value !== "") {
              number_.value = "";
            }
          }
        } else {
          if (liCheck.classList.contains("signup__errorInput")) {
            liCheck.classList.remove("signup__errorInput");
          }
        }
      });

      if (countPlayer === 0) {
        return -4;
      }

      if (countPlayer < minPlayer) {
        return -1;
      }

      if (countPlayer > maxPlayer) {
        return -2;
      }

      if (countCaptain === 0) {
        return -3;
      }

      if (Object.keys(numberDuplicate).length !== countPlayer) {
        for (const key in numberDuplicate) {
          if (numberDuplicate[key].length > 1) {
            numberDuplicate[key].forEach((e) => {
              liChecks[e].classList.add("signup__errorInput");
            });
          }
        }
        return 0;
      }

      //    xóa tất cả phần tử trong bảng trước khi thêm player từ modal
      const resetTable = document.querySelector(
        ".signupTeam__players table tbody"
      );
      resetTable.innerHTML = "";

      for (const key in numberDuplicate) {
        const li = liChecks[numberDuplicate[key][0]];

        const studentID = li.querySelector("input[type='checkbox']").value;
        const captain = li.querySelector("input[type='radio']").checked;
        const number_ = li.querySelector("input[type='number']").value;
        const class_ = li.querySelector("span[data-class]").dataset.class;
        const name = li.querySelector("span[data-name]").dataset.name;
        if (captain) {
          document.getElementById("captainTeam").value = studentID;
        }
        addPlayerToTable(studentID, name, class_, captain, number_);
      }
      return 1;
    }
  }

  // hiển thị lại data player đã chọn vào modal khi submit lỗi
  function loadDataSend() {
    const rowsPlayerTable = document.querySelectorAll(
      ".signupTeam__players table tbody tr"
    );

    if (rowsPlayerTable != null) {
      const captainID = document.getElementById("captainTeam");
      rowsPlayerTable.forEach((row) => {
        const ID = row.querySelector("input[type='checkbox']");
        const number = row.querySelector("input[type='number']");
        const modalPlayerSignup = document.getElementById(
          "studentsSignUpCheckbox"
        );
        const playerCheckbox = modalPlayerSignup.querySelector(
          "#checkbox_" + ID.value
        );
        playerCheckbox.checked = true;
        const playerNumber = modalPlayerSignup.querySelector(
          "#number_" + ID.value
        );
        playerNumber.value = number.value;
        if (captainID.value === ID.value) {
          const playerRadio = modalPlayerSignup.querySelector(
            "#radio_" + ID.value
          );
          playerRadio.checked = true;
        }
      });
    }
  }

  // hiển thị lỗi trên modal khi submit
  const aceptBtn = document.getElementById("aceptAddStudent");
  aceptBtn.addEventListener("click", function () {
    let resultValid = validSignupPlayer();

    const modalError = document.querySelector(".confirmTeamInfo__modal--error");

    if (resultValid === 1) {
      if (modalError !== null && modalError.classList.contains("show")) {
        modalError.classList.remove("show");
      }
      document.getElementById("aceptAddStudentHidden").click();
    }

    if (resultValid === 0) {
      if (modalError !== null && modalError.classList.contains("show")) {
        modalError.classList.remove("show");
      }
    }

    if (resultValid === -1) {
      modalError.textContent =
        "Đội phải có ít nhất " + minPlayer.toString() + " thành viên";
      modalError.classList.add("show");
    }

    if (resultValid === -2) {
      modalError.textContent =
        "Đội chỉ có tối đa " + maxPlayer.toString() + " thành viên";
      modalError.classList.add("show");
    }

    if (resultValid === -3) {
      const modalError = document.querySelector(
        ".confirmTeamInfo__modal--error"
      );
      modalError.textContent = "Chưa chọn đội trưởng";
      modalError.classList.add("show");
    }

    if (resultValid === -4) {
      const modalError = document.querySelector(
        ".confirmTeamInfo__modal--error"
      );
      modalError.textContent = "Chưa chọn thành viên hoặc số áo trống";
      modalError.classList.add("show");
    }
  });

  // xóa phần tử khỏi bảng
  const signUpTableBody = document.querySelector(".signupTeam__players");
  signUpTableBody.addEventListener("click", function (event) {
    if (event.target.classList.contains("btn-danger")) {
      const tr = event.target.closest("tr");
      if (tr) {
        let studentID = tr.querySelector("input[type='checkbox']").value;
        let captain = document.getElementById("captainTeam");
        let number_ = tr.querySelector("input[type='number']").value;
        if (captain.value === studentID) {
          captain.value = "";
          document.querySelector("#radio_" + studentID).checked = false;
        }
        document.querySelector("#checkbox_" + studentID).checked = false;
        document.querySelector("#number_" + studentID).value = "";
        tr.remove();
      }
    }
  });

  // gọi hàm thêm sinh viên lớp 1 vào modal
  const class1 = document.getElementById("class1");
  const inputClass2 = document.getElementById("addClass2");
  if (class1.disabled === false && inputClass2.disabled === false) {
    getStudentSignUp(class1.value, "", 1);
    if (inputClass2.value !== "") {
      getStudentSignUp(class2.value, "studentClass2", 2);
    }
  }
}

if (document.getElementById("changeAvatar") !== null) {
  const inputImg = document.getElementById("changeAvatar");
  const imgShow = document.getElementById("avatarShow");
  inputImg.onchange = function () {
    imgShow.src = URL.createObjectURL(inputImg.files[0]);
  };
}

if (document.getElementById("confirmTeamPage") != null) {
  const UrlParams = new URLSearchParams(window.location.search);

  if (UrlParams.has("teamID")) {
    const teamID = document.getElementById("teamID");
    if (UrlParams.get("teamID") !== null)
      teamID.value = UrlParams.get("teamID");
  }

  if (UrlParams.has("teamStatus")) {
    const teamStatus = document.getElementById("teamStatus");
    if (UrlParams.get("teamStatus") !== null)
      teamStatus.value = UrlParams.get("teamStatus");
  }

  const csrfToken = document
    .querySelector('meta[name="_csrf"]')
    .getAttribute("content");
  const csrfHeaderName = document
    .querySelector('meta[name="_csrf_header"]')
    .getAttribute("content");

  function tournamentCloseSignup() {
    fetch("/api/closeSignup", {
      method: "GET",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then((response) => {
        if (response.status == 502) {
          const error = document.querySelector(".confirmTeam__error");
          error.innerHTML = "<span>Chưa duyệt hết đội</span>";
          return Promise.reject(new Error("Lỗi 502: Chưa duyệt hết đội"));
        }
      })
      .then((data) => {
        window.location.reload();
      })
      .catch((error) => console.error("Error:", error));
  }

  function tournamentOpenSignup() {
    const endDate = document.getElementById("endDate");
    if (endDate.value == "") {
      endDate.focus();
      return;
    }

    const data = {
      endDate: endDate.value,
    };

    fetch("/api/openSignup", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
        [csrfHeaderName]: csrfToken,
      },
      body: JSON.stringify(data),
    })
      .then((response) => {})
      .then((data) => {
        window.location.reload();
      })
      .catch((error) => console.error("Error:", error));
  }
}

if (document.getElementById("teamInfoManager") != null) {
  const confirmTeamTableBody = document.querySelector(
    ".confirmTeamInfo__table"
  );
  confirmTeamTableBody.addEventListener("click", function (event) {
    if (event.target.classList.contains("refuse-btn")) {
      if (event.target.disabled == true) {
        event.target.disabled = false;
      }else if (event.target.disabled == false) {
        event.target.disabled = true;
      }
      const tr = event.target.closest("tr");
      if (tr) {
        const checkboxRefusePlayer = tr.querySelector(
          "input[name='inputRefusePlayers']"
        );
        if (checkboxRefusePlayer.checked == true) {
          checkboxRefusePlayer.checked = false;
        } else if (checkboxRefusePlayer.checked == false) {
          checkboxRefusePlayer.checked = true;
        }
      }
    }
  });

  function aceptTeam() {
    const listRefusePlayer = document.querySelectorAll(
      "input[name='inputRefusePlayers']:checked"
    );
    const players = document.querySelectorAll(
      "input[name='inputRefusePlayers']:not(:checked)"
    );
    const playerIDs = Array.from(players).map((input) => input.value);
    const teamID = document.querySelector("#teamID");
    const aceptPlayerIDs = {
      data: { players: playerIDs, teamID: teamID.value },
    };
    if (listRefusePlayer.length > 0) {
      const showError = document.querySelector(".confirmTeamInfo__error");
      showError.innerHTML = "<span>Có cầu thủ không hợp lệ</span>";
      return;
    }
    const csrfToken = document
      .querySelector('meta[name="_csrf"]')
      .getAttribute("content");
    const csrfHeaderName = document
      .querySelector('meta[name="_csrf_header"]')
      .getAttribute("content");
    fetch("/api/aceptPlayers", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
        [csrfHeaderName]: csrfToken,
      },
      body: JSON.stringify(aceptPlayerIDs),
    })
      .then((response) => {
        if (response.status === 401 || response.status === 403) {
          console.log("Session timeout. Redirecting to login page...");
          window.location.href = "/team/" + teamID.value;
          return;
        }
        if (response.status === 409) {
          const showError = document.querySelector(".confirmTeamInfo__error");
          showError.innerHTML = "<span>Có cầu thủ không hợp lệ</span>";
        }
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
          window.location.href = "/team/" + teamID.value;
          return;
        }
        return;
      })
      .then((data) => {
        window.location.reload();
      })
      .catch((error) => console.error("Error:", error));
  }

    function refusePlayers() {
        const players = document.querySelectorAll(
          "input[name='inputRefusePlayers']:checked"
        );
        const playerIDs = Array.from(players).map((input) => input.value);
        const teamID = document.querySelector("#teamID");
        const refusePlayers = {
          data: { players: playerIDs, teamID: teamID.value },
        };
        const csrfToken = document
          .querySelector('meta[name="_csrf"]')
          .getAttribute("content");
        const csrfHeaderName = document
          .querySelector('meta[name="_csrf_header"]')
          .getAttribute("content");

        fetch("/api/refusePlayers", {
          method: "POST",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
            [csrfHeaderName]: csrfToken,
          },
          body: JSON.stringify(refusePlayers),
        })
          .then((response) => {
            if (response.status === 401 || response.status === 403) {
              // window.location.href = "/team/"+teamID.value;
            }
            if (response.status === 502) {
              // window.location.href = "/team/"+teamID.value;
              const showError = document.querySelector(".confirmTeamInfo__error");
              showError.innerHTML = "<span>Đội không tồn tại</span>";
            }
            if (!response.ok) {
              //                  throw new Error(`HTTP error! status: ${response.status}`);
              // window.location.href = "/team/"+teamID.value;
            }
          })
          .then((data) => {
            window.location.reload();
          })
          .catch((error) => console.error("Error:", error));
    }

    function cancelPlayers() {
        const cancelPlayers_ = document.querySelectorAll(
          "input[name='inputRefusePlayers']:checked"
        );
        const aceptPlayers_ = document.querySelectorAll(
          "input[name='inputRefusePlayers']:not(:checked)"
        );

        const cancelPlayerIDs = Array.from(cancelPlayers_).map((input) => input.value);
        const aceptPlayerIDs = Array.from(aceptPlayers_).map((input) => input.value);
        const teamID = document.querySelector("#teamID");
        const refusePlayers = {
          data: { cancelPlayers: cancelPlayerIDs, aceptPlayers: aceptPlayerIDs, teamID: teamID.value },
        };
        const csrfToken = document
          .querySelector('meta[name="_csrf"]')
          .getAttribute("content");
        const csrfHeaderName = document
          .querySelector('meta[name="_csrf_header"]')
          .getAttribute("content");

        fetch("/api/cancelPlayers", {
          method: "POST",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
            [csrfHeaderName]: csrfToken,
          },
          body: JSON.stringify(refusePlayers),
        })
          .then((response) => {
            if (response.status === 401 || response.status === 403) {
              // window.location.href = "/team/"+teamID.value;
            }
            if (response.status === 502) {
              // window.location.href = "/team/"+teamID.value;
              const showError = document.querySelector(".confirmTeamInfo__error");
              showError.innerHTML = "<span>Không thể thực hiện</span>";
            }
            if(response.status === 409){
                return response.json().then((error) => Promise.reject(error));
            }
          })
          .then((data) => {
            window.location.reload();
          })
          .catch((error) => {
                const errorShow = document.querySelector(".confirmTeamInfo__error");
                errorShow.innerHTML = "<span>"+error.messager+"</span>";
          });
    }

        function cancelTeam() {
            const teamID = document.querySelector("#teamID");
            const data = {
              data: teamID.value
            };
            const csrfToken = document
              .querySelector('meta[name="_csrf"]')
              .getAttribute("content");
            const csrfHeaderName = document
              .querySelector('meta[name="_csrf_header"]')
              .getAttribute("content");

            fetch("/api/cancelTeam", {
              method: "POST",
              credentials: "include",
              headers: {
                "Content-Type": "application/json",
                [csrfHeaderName]: csrfToken,
              },
              body: JSON.stringify(data),
            })
              .then((response) => {
                if (response.status === 401 || response.status === 403) {
                  // window.location.href = "/team/"+teamID.value;
                }
                if (response.status === 502) {
                  // window.location.href = "/team/"+teamID.value;
                  const showError = document.querySelector(".confirmTeamInfo__error");
                  showError.innerHTML = "<span>Không thể thực hiện</span>";
                }
                if (!response.ok) {
                  //                  throw new Error(`HTTP error! status: ${response.status}`);
                  // window.location.href = "/team/"+teamID.value;
                }
              })
              .then((data) => {
                window.location.reload();
              })
              .catch((error) => console.error("Error:", error));
        }

        function aceptTeamInprogress() {
            const teamID = document.querySelector("#teamID");
            const data = {
              data: teamID.value
            };
            const csrfToken = document
              .querySelector('meta[name="_csrf"]')
              .getAttribute("content");
            const csrfHeaderName = document
              .querySelector('meta[name="_csrf_header"]')
              .getAttribute("content");

            fetch("/api/aceptTeamInprogress", {
              method: "POST",
              credentials: "include",
              headers: {
                "Content-Type": "application/json",
                [csrfHeaderName]: csrfToken,
              },
              body: JSON.stringify(data),
            })
              .then((response) => {
                if (response.status === 401 || response.status === 403) {
                  // window.location.href = "/team/"+teamID.value;
                }
                if (response.status === 502) {
                  // window.location.href = "/team/"+teamID.value;
                  const showError = document.querySelector(".confirmTeamInfo__error");
                  showError.innerHTML = "<span>Không thể thực hiện</span>";
                }
                if (!response.ok) {
                  //                  throw new Error(`HTTP error! status: ${response.status}`);
                  // window.location.href = "/team/"+teamID.value;
                }
              })
              .then((data) => {
                window.location.reload();
              })
              .catch((error) => console.error("Error:", error));
        }
}

if (document.getElementById("teamInfomationPlayers") != null) {
  function cancelSignup_() {
    const teamID = document.getElementById("teamID");
    const csrfToken = document
      .querySelector('meta[name="_csrf"]')
      .getAttribute("content");
    const csrfHeaderName = document
      .querySelector('meta[name="_csrf_header"]')
      .getAttribute("content");

    fetch("/api/cancelSignup", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
        [csrfHeaderName]: csrfToken,
      },
      body: JSON.stringify({ data: teamID.value }),
    })
      .then((response) => {
        if (!response.ok) {
          window.location.href = "/";
        }
      })
      .then((data) => {
        window.location.href = "/";
      })
      .catch((error) => console.error("Error:", error));
  }

  function changeCaptain(){
    const selectedCaptain = document.querySelector('input[name="new_captain"]:checked').value;
    const currentCaptain = document.getElementById('current_captain').value;
    let data = {
            current_captain: currentCaptain,
            new_captain: selectedCaptain
        };
    const csrfToken = document
      .querySelector('meta[name="_csrf"]')
      .getAttribute("content");
    const csrfHeaderName = document
      .querySelector('meta[name="_csrf_header"]')
      .getAttribute("content");
    fetch('/api/changeCaptain', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeaderName]: csrfToken,
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (!response.ok) {
          window.location.href = "/";
        }
    })
    .then(data => {
        window.location.reload();
    })
    .catch(error => {
        console.error('Error:', error);
    });
  }
}

if (document.getElementById("createDrawForm") != null) {
  function confirmCreateDrawSubmit() {
    document.getElementById("createDrawForm").submit();
  }
}

// tao lich thi dau
if (document.getElementById("firstHalf") != null) {
  function submitSchedule() {
    const firstHalf = document.getElementById("timeHalf");
    const breakTime = document.getElementById("breakTime");
    const openday = document.getElementById("openday");
    const closeday = document.getElementById("closeday");
    const morningStart = document.getElementById("morningStart");
    const morningEnd = document.getElementById("morningEnd");
    const eveningStart = document.getElementById("eveningStart");
    const eveningEnd = document.getElementById("eveningEnd");
    const sameTime = document.getElementById("sameTime");
    const break2Match = document.getElementById("break2Match");

    const error = document.querySelector(".createSchedule__error");
    if (
      firstHalf.value === "" ||
      breakTime.value === "" ||
      openday.value === "" ||
      closeday.value === "" ||
      morningStart.value === "" ||
      morningEnd.value === "" ||
      eveningStart.value === "" ||
      eveningEnd.value === "" ||
      sameTime.value === "" ||
      break2Match.value === ""
    ) {
      error.innerHTML = "Các mục phải được chọn hết";
      return false;
    }
  }
}

if (document.getElementById("startDate1") !== null) {
  function submitSchdedule() {
    const startDate = document.getElementById("startDate1");
    const endDate = document.getElementById("endDate1");
    const timeMorningStart = document.getElementById("timeMorningStart");
    const timeMorningEnd = document.getElementById("timeMorningEnd");
    const timeEveningStart = document.getElementById("timeEveningStart");
    const timeEveningEnd = document.getElementById("timeEveningEnd");
    const breakTimeTeam = document.getElementById("breakTimeTeam");
    const matchSameTime = document.getElementById("matchSameTime");
    let start = new Date(startDate.value);
    let end = new Date(endDate.value);

    const elementsToCheck = [
      startDate,
      endDate,
      timeMorningStart,
      timeMorningEnd,
      timeEveningStart,
      timeEveningEnd,
      breakTimeTeam,
      matchSameTime,
    ];

    for (const element of elementsToCheck) {
      if (!element || element.value === "") {
        element.focus();
        return false;
      }
    }

    if (startDate.value === "") {
      startDate.focus();
      return false;
    } else if (endDate.value === "") {
      endDate.focus();
      return false;
    } else if (start >= end) {
      startDate.focus();
      endDate.focus();
      return false;
    }
  }

  function confirmSubmit() {
    document.getElementById("createScheduleForm").submit();
  }

  function getInfomationTeam(matchID) {
    return new Promise((resolve, reject) => {
      // Bổ sung
      const csrfToken = document
        .querySelector('meta[name="_csrf"]')
        .getAttribute("content");
      const csrfHeaderName = document
        .querySelector('meta[name="_csrf_header"]')
        .getAttribute("content");

      fetch("/api/getInfoMatch", {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
          [csrfHeaderName]: csrfToken,
        },
        body: JSON.stringify({ data: matchID }),
      })
        .then((response) => {
          if (response.status === 401 || response.status === 403) {
            window.location.href = "/index";
            reject(new Error("Unauthorized or Forbidden"));
            return null;
          }

          if (response.status === 409) {
            window.location.href = "/index";
            reject(new Error("Unauthorized or Forbidden"));
            return null;
          }
          return response.json();
        })
        .then((data) => {
          if (data) {
            // Kiểm tra xem data có tồn tại không trước khi xử lý
            document.getElementById("matchID").value = data.id;

            document.getElementById("matchInfo__team1").textContent =
              data.team1.class1 +
              (data.team1.class2 ? " - " + data.team1.class2 : "");
            document.getElementById("matchInfo__team2").textContent =
              data.team2.class1 +
              (data.team2.class2 ? " - " + data.team2.class2 : "");

            const dateParts = data.date.split("-");
            const formattedDate = `${dateParts[2]}/${dateParts[1]}/${dateParts[0]}`;
            document.getElementById("matchInfo__date").textContent =
              formattedDate;

            const formattedTime = data.time.substring(0, 5);
            document.getElementById("matchInfo__time").textContent =
              formattedTime;

            resolve(data);
          }
        })
        .catch((error) => {
          console.error("Error:", error);
          reject(error);
        });
    });
  }

  function changeInfoTeamCheck() {
    const changeDate = document.getElementById("changeDate");
    const changeTime = document.getElementById("changeTime");
    const matchID = document.getElementById("matchID");

    const showError = document.querySelector(
      ".changeSchedule__modal--error span"
    );

    if (changeDate.value !== "" && changeTime.value !== "") {
      const csrfToken = document
        .querySelector('meta[name="_csrf"]')
        .getAttribute("content");
      const csrfHeaderName = document
        .querySelector('meta[name="_csrf_header"]')
        .getAttribute("content");

      const data = {
        matchID: matchID.value,
        changeDate: changeDate.value,
        changeTime: changeTime.value,
      };

      fetch("/api/changeInfoMatchCheck", {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
          [csrfHeaderName]: csrfToken,
        },
        body: JSON.stringify(data),
      })
        .then((response) => {
          if (response.ok) return response.json();

          if (response.status === 401 || response.status === 403) {
            window.location.href = "/index";
            return null;
          }
          if (response.status === 409) {
            return response.json().then((error) => Promise.reject(error));
          }

          return Promise.reject(new Error("Unexpected error"));
        })
        .then((data) => {
          //              window.location.reload();
          if (data.success) {
            showError.classList.remove("error");
            showError.classList.add("success");
            showError.textContent = data.success;
          }
        })
        .catch((error) => {
          showError.classList.remove("success");
          showError.classList.add("error");
          showError.innerHTML = "";
          if (error.errorMessager) {
            showError.textContent = error.errorMessager;
          } else {
            const ul = document.createElement("ul");
            showError.appendChild(ul);

            if (error.team1Schedule) {
              const li = document.createElement("li");
              li.textContent = error.team1Schedule;
              ul.appendChild(li);
            }

            if (error.team2Schedule) {
              const li = document.createElement("li");
              li.textContent = error.team2Schedule;
              ul.appendChild(li);
            }

            if (error.sameTimeMatch) {
              const li = document.createElement("li");
              li.textContent = error.sameTimeMatch;
              ul.appendChild(li);
            }
          }
        });

      // enable button submit
      const submitBtn = document.getElementById("submit__button");
      submitBtn.disabled = false;
    }
  }

  function changeInfomationMatch() {
    const changeDate = document.getElementById("changeDate");
    const changeTime = document.getElementById("changeTime");
    const matchID = document.getElementById("matchID");

    const csrfToken = document
      .querySelector('meta[name="_csrf"]')
      .getAttribute("content");
    const csrfHeaderName = document
      .querySelector('meta[name="_csrf_header"]')
      .getAttribute("content");

    const data = {
      matchID: matchID.value,
      changeDate: changeDate.value,
      changeTime: changeTime.value,
    };

    fetch("/api/changeInfoMatch", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
        [csrfHeaderName]: csrfToken,
      },
      body: JSON.stringify(data),
    })
      .then((response) => {
        if (!response.ok) {
          window.location.href = "/index";
          return null;
        }
      })
      .then((data) => {
        window.location.reload();
      })
      .catch((error) => console.log(error));
  }

  document.getElementById("confirmChangeSchedule")
    .addEventListener("show.bs.modal", function () {
      let errorSpan = document.querySelector('.changeSchedule__modal--error span');
      let confirmSpan = document.querySelector('#confirmChangeSchedule__error span');
      if (errorSpan.classList.contains("error")) {
        confirmSpan.textContent =
          "Có cảnh báo, bạn vẫn muốn tiếp tục đổi lịch thi đấu ?";
      } else {
        confirmSpan.textContent =
          "Không có lỗi, xác nhận để đổi lịch thi đấu ?";
      }
    });

  let changeInfoBtns = document.querySelectorAll(".open-modal");
  changeInfoBtns.forEach(function (button) {
    button.addEventListener("click", function () {
      const td = this.closest("td");
      const hiddenInput = td.querySelector('input[type="hidden"]');
      const matchID = hiddenInput.value;
      getInfomationTeam(matchID).then((data) => {
        const hiddenBtn = td.querySelector(".hidden");
        if (hiddenBtn) {
          hiddenBtn.click();
        }
      });
    });
  });

  let confirmChangeScheduleModal = document.getElementById(
    "confirmChangeSchedule"
  );

  let customBackdrop = document.createElement("div");
  customBackdrop.classList.add("modal-backdrop", "custom-backdrop");

  confirmChangeScheduleModal.addEventListener("show.bs.modal", function () {
    document.body.appendChild(customBackdrop);
    customBackdrop.style.display = "block";
  });

  confirmChangeScheduleModal.addEventListener("hide.bs.modal", function () {
    customBackdrop.style.display = "none";
    document.body.removeChild(customBackdrop);
  });

    const UrlParams = new URLSearchParams(window.location.search);

    if (UrlParams.has("matchTime")) {
      const dateInput = document.getElementById("matchTime");
      if (UrlParams.get("matchTime") !== null)
        dateInput.value = UrlParams.get("matchTime");
    }

    if (UrlParams.has("matchDate")) {
      const timeInput = document.getElementById("matchDate");
      if (UrlParams.get("matchDate") !== null)
        timeInput.value = UrlParams.get("matchDate");
    }
}


if (document.getElementById("matchInfo") !== null) {
    function attendanceSubmit(){
        const form = document.querySelector('#attendanceForm');
        const attendanceList = document.querySelectorAll('input[name="attendanceList"]:checked').length;
        const mainPlayers = document.querySelectorAll('input[name="mainPlayers"]:checked').length;
        if (mainPlayers > 5) {
            document.querySelector("#error_mainPlayer").innerHTML = "Vượt quá 5 cầu thủ đá chính";
            return false;
        }
        if(attendanceList === 0){
            document.querySelector("#error_mainPlayer").innerHTML = "Chưa chọn cầu thủ ra sân";
            return false;
        }
        if(mainPlayers === 0){
            document.querySelector("#error_mainPlayer").innerHTML = "Chưa chọn cầu thủ đá chính";
            return false;
        }
        return true;
    }

}