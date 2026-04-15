/**
 * auth.js - Authentication Logic
 */
const auth = {
    // Perform Login
    login: async (username, password) => {
        try {
            const result = await api.post('/auth/login', { username, password });
            
            // Backend ApiResponse uses 'status: 200' instead of 'success: true'
            if (result.status === 200) {
                const loginData = result.data;
                api.setToken(loginData.token);
                // Save full user info for use in checkout/profile
                api.setUser({
                    username:    loginData.username,
                    role:        loginData.role,
                    hoTen:       loginData.hoTen       || loginData.username,
                    email:       loginData.email        || '',
                    soDienThoai: loginData.soDienThoai  || '',
                    diaChi:      loginData.diaChi        || ''
                });
                
                api.showToast("Đăng nhập thành công!", "success");

                // Immediately update header dropdown — no reload needed
                if (window.layout && layout.updateUserHeader) layout.updateUserHeader();

                // Redirect based on role
                if (loginData.role === 'ADMIN' || loginData.role === 'STAFF') {
                    setTimeout(() => layout.render('Dashboard', 'Admin/Index'), 500);
                } else {
                    setTimeout(() => layout.render('Home', 'Index'), 500);
                }
            }
        } catch (error) {
            api.showToast(error.message, "error");
        }
    },

    // Perform Registration
    register: async (userData) => {
        try {
            const result = await api.post('/auth/register', userData);
            if (result.status === 200) {
                api.showToast("Đăng ký thành công! Hãy đăng nhập.", "success");
                layout.render('Auth', 'Login');
            }
        } catch (error) {
            api.showToast(error.message, "error");
        }
    },

    // Logout
    logout: () => {
        api.clearToken();
        api.clearUser();
        sessionStorage.removeItem('applied_voucher');
        // Immediately update header dropdown to guest state — no page reload needed
        if (window.layout && layout.updateUserHeader) layout.updateUserHeader();
        api.showToast("Đã đăng xuất thành công");
        layout.render('Home', 'Index');
    }
};

// Form Binding Logic (to be called after view render)
function initAuthForms() {
    $("#loginForm").on('submit', function(e) {
        e.preventDefault();
        const user = $("#username").val();
        const pass = $("#password").val();
        auth.login(user, pass);
    });

    $("#registerForm").on('submit', function(e) {
        e.preventDefault();
        const pass = $("#reg-password").val();
        const confirm = $("#reg-confirm-password").val();
        
        if (pass !== confirm) {
            api.showToast("Mật khẩu xác nhận không khớp", "error");
            return;
        }

        const data = {
            username: $("#reg-username").val(),
            email: $("#reg-email").val(),
            password: pass
        };
        auth.register(data);
    });
}
