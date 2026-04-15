/**
 * support.js - AI Chat Support & Customer Tickets Logic
 */
const support = {
    // Initialize Chat Widget
    initChat: () => {
        $("#chat-form").off("submit").on("submit", function(e) {
            e.preventDefault();
            const msg = $("#chat-user-msg").val().trim();
            if (msg) {
                support.sendChat(msg);
                $("#chat-user-msg").val("");
            }
        });
    },

    sendChat: async (message) => {
        const chatBox = $("#chat-box");
        chatBox.append(`
            <div class="message-user bg-accent text-white p-3 rounded-4 shadow-sm align-self-end text-end" style="max-width: 80%;">
                ${message}
            </div>
        `);
        support.scrollToBottom();

        const typingId = "typing-" + Date.now();
        chatBox.append(`
            <div id="${typingId}" class="message-ai bg-white p-3 rounded-4 shadow-sm align-self-start" style="max-width: 80%;">
                <span class="spinner-grow spinner-grow-sm text-accent"></span> AI đang suy nghĩ...
            </div>
        `);
        support.scrollToBottom();

        try {
            const res = await api.post(`/support/ai-chat?message=${encodeURIComponent(message)}`);
            $(`#${typingId}`).remove();
            if (res.status === 200) {
                chatBox.append(`
                    <div class="message-ai bg-white p-3 rounded-4 shadow-sm align-self-start" style="max-width: 80%;">
                        ${res.data || "Xin lỗi, tôi chưa hiểu ý bạn."}
                    </div>
                `);
            } else { throw new Error(); }
        } catch (e) {
            $(`#${typingId}`).remove();
            chatBox.append(`<div class="message-ai bg-white p-3 rounded-4 shadow-sm align-self-start text-danger">Hệ thống bận.</div>`);
        }
        support.scrollToBottom();
    },

    scrollToBottom: () => {
        const chatBox = $("#chat-box");
        if(chatBox.length) chatBox.scrollTop(chatBox[0].scrollHeight);
    },

    toggleChat: () => {
        const holder = $("#chat-box-holder");
        console.log("Toggle Chat triggered. Children:", holder.children().length);

        if (holder.children().length === 0) {
            holder.load("Support/Chat.html", function(response, status, xhr) {
                if (status === "error") {
                    api.showToast("Lỗi nạp Chatbot", "error");
                    return;
                }
                support.initChat();
                holder.fadeIn();
            });
        } else {
            holder.fadeToggle();
        }
    }
};
