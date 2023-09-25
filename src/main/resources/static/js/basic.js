let isLoggingOut = false;  // 로그아웃 중인지 확인하는 전역 플래그

function logout() {
    isLoggingOut = true;  // 로그아웃 중임을 표시
    localStorage.removeItem("Access_Token");
    localStorage.removeItem("Refresh_Token");
    location.reload();
}

window.onload = function() {
    if (isLoggingOut) {
        return;  // 로그아웃 중일 경우, 나머지 로직을 건너뛴다.
    }

    const token = localStorage.getItem('Access_Token');
    const currentPath = window.location.pathname;

    if (!token) {
        // 토큰이 없는 경우 (비회원)
        document.getElementById('productManagement').style.display = 'none';
        document.getElementById('storeManagement').style.display = 'none';
        document.getElementById('orderManagementLink').style.display = 'none';
    } else {
        const decodedToken = atob(token.split('.')[1]);
        const payload = JSON.parse(decodedToken);
        const userType = payload.userType;

        if (userType === 'SELLER') {
            // SELLER인 경우 모든 메뉴가 보임
            document.getElementById('productManagement').style.display = 'block';
            document.getElementById('storeManagement').style.display = 'block';
            document.getElementById('orderManagementLink').setAttribute('href', '/seller3');
        } else if (userType === 'USER') {
            // USER인 경우 상품 목록, 주문 관리만 보임
            document.getElementById('productManagement').style.display = 'none';
            document.getElementById('storeManagement').style.display = 'none';
            document.getElementById('orderManagementLink').setAttribute('href', '/orders');
        } else {
            // 그 외 경우 (예: 토큰은 있지만 userType이 SELLER나 USER가 아닌 경우)
            document.getElementById('productManagement').style.display = 'none';
            document.getElementById('storeManagement').style.display = 'none';
            document.getElementById('orderManagementLink').style.display = 'none';
        }

        const username = payload.sub;

        const greetingMessage = `[${userType}] ${username}  `;
        const usernameSpan = document.getElementById('usernameSpan');
        usernameSpan.textContent = greetingMessage;

        const loggedOutButtons = document.getElementById('loggedOutButtons');
        loggedOutButtons.style.display = 'none';
        document.getElementById('loggedInInfo').style.display = 'block';
    }
}