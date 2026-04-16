/**
 * support.js - AI Chat Support & Customer Tickets Logic
 * Standardized for Full English Backend synchronization
 */
let currentTicketId = null;

const support = {
    sessionId: null,

    // Initialize Chat Widget — gắn event vào input trong widget mới
    initChat: () => {
        support.initSessionId();
        setTimeout(() => $("#chat-user-msg").focus(), 200);
    },

    initSessionId: () => {
        if (!support.sessionId) {
            support.sessionId = 'sess-' + Math.random().toString(36).substr(2, 9) + '-' + Date.now();
        }
    },

    // Send from input field
    sendChatFromInput: () => {
        const input = $("#chat-user-msg");
        const msg = input.val().trim();
        if (!msg) return;
        input.val("");
        support.sendChat(msg);
    },

    // Escape HTML to prevent XSS
    escapeHtml: (text) => $('<div>').text(text).html(),

    // Render markdown-lite: **bold**, newlines, bullet points
    renderMarkdown: (text) => {
        if (!text) return "";
        let html = support.escapeHtml(text)
            .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
            .replace(/\*(.*?)\*/g, '<em>$1</em>')
            .replace(/^\s*•\s*(.*)/gm, '<li>$1</li>')
            .replace(/\n/g, '<br>');
        
        if (html.includes('<li>')) {
            html = html.replace(/(<li>.*<\/li>)/s, '<ul>$1</ul>');
        }
        return html;
    },

    sendChat: async (message) => {
        const chatBox = $("#chat-box");
        if (!chatBox.length) return;

        support.initSessionId();

        // Remove quick replies (they become stale after user picks one)
        chatBox.find('.bsw-quick-replies').remove();

        // User bubble
        chatBox.append(`
            <div class="bsw-msg-user">
                <div class="bsw-bubble">${support.escapeHtml(message)}</div>
            </div>
        `);
        support.scrollToBottom();

        // Typing indicator
        const typingId = "typing-" + Date.now();
        chatBox.append(`
            <div id="${typingId}" class="bsw-msg-ai">
                <div class="bsw-avatar" style="background: linear-gradient(135deg, #1A73E8, #9B72CB, #D96570); box-shadow: 0 3px 8px rgba(155, 114, 203, 0.2);">✨</div>
                <div class="bsw-bubble" style="color:#1A73E8; display:flex; align-items:center; gap:4px; border:none; background:transparent; box-shadow:none;">
                    <span style="animation:bsw-dot 1.2s infinite 0s">●</span>
                    <span style="animation:bsw-dot 1.2s infinite 0.2s">●</span>
                    <span style="animation:bsw-dot 1.2s infinite 0.4s">●</span>
                </div>
            </div>
        `);

        // Add dot animation style once
        if (!document.getElementById('bsw-dot-style')) {
            $('<style id="bsw-dot-style">@keyframes bsw-dot{0%,80%,100%{opacity:.2;transform:scale(.8)}40%{opacity:1;transform:scale(1)}}</style>').appendTo('head');
        }

        support.scrollToBottom();

        try {
            const url = `/support/ai-chat?message=${encodeURIComponent(message)}&sessionId=${support.sessionId}`;
            const res = await api.post(url);
            $(`#${typingId}`).remove();

            // Parse response: {message, quickReplies, sessionId}
            const data = res.data;
            const text   = (typeof data === 'object' && data.message) ? data.message :
                           (typeof data === 'string' ? data : 'Xin lỗi, tôi chưa hiểu ý bạn.');

            // AI bubble (Google Gemini Style with Fade In Effect)
            chatBox.append(`
                <div class="bsw-msg-ai" style="animation: bsw-fade-in 0.4s cubic-bezier(0.16, 1, 0.3, 1);">
                    <div class="bsw-avatar" style="background: linear-gradient(135deg, #1A73E8, #9B72CB, #D96570); box-shadow: 0 3px 8px rgba(155, 114, 203, 0.2);">✨</div>
                    <div class="bsw-bubble" style="background: linear-gradient(135deg, rgba(26,115,232,0.05), rgba(155,114,203,0.05)); border: 1px solid rgba(155,114,203,0.1);">${support.renderMarkdown(text)}</div>
                </div>
            `);

            if (data.quickReplies && data.quickReplies.length > 0) {
                support.appendQuickReplies(data.quickReplies);
            }

        } catch (e) {
            $(`#${typingId}`).remove();
            chatBox.append(`
                <div class="bsw-msg-ai" style="animation: bsw-fade-in 0.4s ease;">
                    <div class="bsw-avatar" style="background: linear-gradient(135deg, #1A73E8, #9B72CB, #D96570); box-shadow: 0 3px 8px rgba(155, 114, 203, 0.2);">✨</div>
                    <div class="bsw-bubble" style="color:#D96570; border: 1px solid rgba(217, 101, 112, 0.2);">
                        Hệ thống đang bận, vui lòng thử lại sau nhé!
                    </div>
                </div>
            `);
        }
        support.scrollToBottom();
    },

    appendQuickReplies: (replies) => {
        const chatBox = $("#chat-box");
        let html = '<div class="bsw-quick-replies d-flex flex-wrap gap-2 mt-2 px-5" style="animation: bsw-fade-in 0.6s ease;">';
        replies.forEach(r => {
            html += `<button class="btn btn-sm btn-outline-primary rounded-pill bg-white" onclick="support.sendChat('${r.replace(/'/g, "\\'")}')" style="font-size: 0.85rem; border-color: rgba(26,115,232,0.3); color: #1A73E8;">${r}</button>`;
        });
        html += '</div>';
        chatBox.append(html);
    },

    scrollToBottom: () => {
        const chatBox = $("#chat-box");
        if (chatBox.length) chatBox.scrollTop(chatBox[0].scrollHeight);
    },

    toggleChat: () => {
        const holder = $("#chat-box-holder");
        if (holder.children().length === 0) {
            holder.load("Shared/ChatWidget.html", function(response, status, xhr) {
                if (status === "error") {
                    api.showToast("Không thể tải chatbot", "error");
                    return;
                }
                support.initChat();
                holder.fadeIn(200);
            });
        } else {
            holder.fadeToggle(200);
        }
    },

    /**
     * Load user's own support tickets (Customer view - 4 cols)
     */
    loadUserTickets: async () => {
        const user = api.getUser();
        const tbody = $("#support-list-body");
        if (!tbody.length) return;

        tbody.html(`
            <tr><td colspan="4" class="text-center py-5">
                <div class="spinner-border spinner-border-sm text-secondary" role="status"></div>
                <span class="ms-2 text-muted small">Đang tải...</span>
            </td></tr>
        `);

        if (!user) {
            tbody.html('<tr><td colspan="4" class="text-center py-5 text-muted">Vui lòng đăng nhập để xem yêu cầu hỗ trợ.</td></tr>');
            return;
        }

        try {
            const res = await api.get(`/support/user/${user.username}`);
            const tickets = res.data || [];
            tbody.empty();

            if (tickets.length === 0) {
                tbody.html('<tr><td colspan="4" class="text-center py-5 text-muted">Bạn chưa có yêu cầu hỗ trợ nào.</td></tr>');
                return;
            }

            tickets.forEach(ticket => {
                tbody.append(`
                    <tr>
                        <td class="ps-4 fw-bold">${ticket.subject || ticket.title || "---"}</td>
                        <td>${ticket.createdAt ? new Date(ticket.createdAt).toLocaleDateString('vi-VN') : '---'}</td>
                        <td>${support.getStatusBadge(ticket.status)}</td>
                        <td class="text-end pe-4">
                            <span class="text-muted small">#${ticket.id}</span>
                        </td>
                    </tr>
                `);
            });
        } catch (e) {
            tbody.html('<tr><td colspan="4" class="text-center py-5 text-danger">Lỗi khi tải danh sách yêu cầu.</td></tr>');
        }
    },

    /**
     * Open modal to create new ticket
     */
    openCreateTicket: () => {
        $("#support-title").val("");
        $("#support-content").val("");
        const modal = new bootstrap.Modal(document.getElementById("support-modal"));
        modal.show();
    },

    /**
     * Submit new ticket from modal
     */
    submitTicket: async () => {
        const user = api.getUser();
        if (!user) { api.showToast("Vui lòng đăng nhập", "warning"); return; }

        const subject = $("#support-title").val().trim();
        const content = $("#support-content").val().trim();

        if (!subject || !content) {
            api.showToast("Vui lòng điền đầy đủ thông tin", "warning");
            return;
        }

        const btn = $("button[onclick='support.submitTicket()']").text("Đang gửi...").prop("disabled", true);
        try {
            await api.post(`/support?username=${encodeURIComponent(user.username)}&subject=${encodeURIComponent(subject)}&content=${encodeURIComponent(content)}`);
            api.showToast("Đã gửi yêu cầu hỗ trợ thành công!", "success");
            bootstrap.Modal.getInstance(document.getElementById("support-modal")).hide();
            support.loadUserTickets();
        } catch (e) {
            api.showToast("Gửi yêu cầu thất bại: " + e.message, "error");
        } finally {
            btn.text("Gửi yêu cầu").prop("disabled", false);
        }
    },

    /**
     * Load admin support tickets list
     */
    loadAdminTickets: async () => {
        try {
            const res = await api.get('/api/support');
            const data = Array.isArray(res) ? res : (res.data || []);
            const tbody = $("#support-list-body");
            if (!tbody.length) return;
            tbody.empty();

            data.forEach(t => {
                const statusBadge = support.getStatusBadge(t.statusCode || t.status);
                tbody.append(`
                    <tr onclick="layout.render('Support/Admin', 'Details', '${t.ticketId || t.id}')" style="cursor:pointer">
                        <td class="ps-4 fw-bold">#${t.ticketId || t.id}</td>
                        <td>${t.customerName || '---'}</td>
                        <td class="text-truncate" style="max-width: 250px;">${t.title || t.subject}</td>
                        <td>${statusBadge}</td>
                        <td>${new Date(t.createdAt).toLocaleDateString('en-GB')}</td>
                        <td class="text-end pe-4">
                            <button class="btn btn-sm btn-light rounded-circle"><i class="icon icon-arrow-right"></i></button>
                        </td>
                    </tr>
                `);
            });
        } catch (e) { api.showToast("Không thể tải danh sách phiếu hỗ trợ", "error"); }
    },

    /**
     * Load ticket details (Admin)
     */
    loadTicketDetails: async (id) => {
        currentTicketId = id;
        try {
            const res = await api.get('/api/support');
            const list = Array.isArray(res) ? res : (res.data || []);
            const t = list.find(x => (x.ticketId || x.id) == id);
            
            if (t) {
                const title = t.title || t.subject;
                const statusCode = t.statusCode || t.status;
                const ticketId = t.ticketId || t.id;

                $("#ticket-detail-title").text(title);
                $("#ticket-detail-id").text(`ID: #${ticketId}`);
                $("#ticket-detail-content").text(t.content);
                $("#ticket-customer-name").text(t.customerName || 'Customer');
                $("#ticket-created-date").text(new Date(t.createdAt).toLocaleString());
                
                const badge = $("#ticket-status-badge");
                badge.text(statusCode).removeClass().addClass(`badge rounded-pill px-3 py-2 ${support.getStatusClass(statusCode)}`);
                
                $("#target-status").val(statusCode);
                $("#admin-reply").val(t.adminReply || '');
                $("#internal-note").val(t.internalNote || '');

                $("#comments-list").empty().append(`
                    <div class="mb-3 p-3 bg-light rounded-3">
                        <small class="text-muted d-block mb-1">Yêu cầu từ khách hàng:</small>
                        <div class="fw-medium">${t.content}</div>
                    </div>
                `);

                if (t.adminReply) {
                    $("#comments-list").append(`
                        <div class="mb-3 p-3 bg-accent bg-opacity-10 rounded-3 border-start border-accent border-4">
                            <small class="text-accent fw-bold d-block mb-1">Phản hồi của Admin:</small>
                            <div>${t.adminReply}</div>
                        </div>
                    `);
                }

                if (t.internalNote) {
                    $("#comments-list").append(`
                        <div class="mb-2 p-3 bg-warning bg-opacity-10 rounded-3 border-start border-warning border-4">
                            <small class="text-warning fw-bold d-block mb-1">Ghi chú nội bộ:</small>
                            <div class="small italic text-muted">${t.internalNote}</div>
                        </div>
                    `);
                }
            }
        } catch (e) { api.showToast("Lỗi khi tải chi tiết phiếu hỗ trợ", "error"); }
    },

    /**
     * Submit Response (Amin Handled logic)
     */
    submitResponse: async () => {
        const id = currentTicketId;
        const reply = $("#admin-reply").val().trim();
        const note = $("#internal-note").val().trim();
        const status = $("#target-status").val();

        api.showToast("Đang cập nhật...", "info");
        try {
            await api.post(`/api/support/${id}/respond?reply=${encodeURIComponent(reply)}&internalNote=${encodeURIComponent(note)}&statusCode=${status}`);
            api.showToast("✓ Cập nhật hồ sơ thành công!", "success");
            support.loadTicketDetails(id);
        } catch (e) {
            api.showToast("Lỗi cập nhật: " + e.message, "error");
        }
    },

    /**
     * Get status badge HTML
     */
    getStatusBadge: (status) => {
        const cls = support.getStatusClass(status);
        return `<span class="badge rounded-pill px-3 ${cls}">${status}</span>`;
    },

    /**
     * Get status CSS class
     */
    getStatusClass: (status) => {
        switch (status) {
            case 'OPEN': return 'bg-danger';
            case 'PROCESSING': return 'bg-warning text-dark';
            case 'RESOLVED': return 'bg-success';
            case 'CLOSED': return 'bg-secondary';
            default: return 'bg-dark';
        }
    },

    confirmDelete: async () => {
        if (!confirm("Bạn có chắc chắn muốn xóa yêu cầu hỗ trợ này không?")) return;
        api.showToast("Tính năng này đã bị vô hiệu hóa vì lý do an toàn hệ thống", "warning");
    }
};

// Global toggle event
$(document).on('click', '#btn-toggle-chat', () => support.toggleChat());
