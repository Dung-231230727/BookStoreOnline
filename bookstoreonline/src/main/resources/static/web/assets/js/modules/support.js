/**
 * support.js - AI Chat Support & Customer Tickets Logic
 * Standardized for Full English Backend synchronization
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
                <span class="spinner-grow spinner-grow-sm text-accent"></span> AI is thinking...
            </div>
        `);
        support.scrollToBottom();

        try {
            const res = await api.post(`/support/ai-chat?message=${encodeURIComponent(message)}`);
            $(`#${typingId}`).remove();
            if (res.status === 200) {
                chatBox.append(`
                    <div class="message-ai bg-white p-3 rounded-4 shadow-sm align-self-start" style="max-width: 80%;">
                        ${res.data || "I'm sorry, I don't understand."}
                    </div>
                `);
            } else { throw new Error(); }
        } catch (e) {
            $(`#${typingId}`).remove();
            chatBox.append(`<div class="message-ai bg-white p-3 rounded-4 shadow-sm align-self-start text-danger">System busy.</div>`);
        }
        support.scrollToBottom();
    },

    scrollToBottom: () => {
        const chatBox = $("#chat-box");
        if(chatBox.length) chatBox.scrollTop(chatBox[0].scrollHeight);
    },

    toggleChat: () => {
        const holder = $("#chat-box-holder");
        if (holder.children().length === 0) {
            holder.load("Shared/ChatWidget.html", function(response, status, xhr) {
                if (status === "error") {
                    api.showToast("Failed to load Chatbot", "error");
                    return;
                }
                support.initChat();
                holder.fadeIn();
            });
        } else {
            holder.fadeToggle();
        }
    },

    /**
     * Load admin support tickets list
     */
    loadAdminTickets: async () => {
        try {
            const search = $("#support-search").val() || "";
            const status = $("#support-status-filter").val() || "";
            const priority = $("#support-priority-filter").val() || "";

            const res = await api.get(`/support`, { search, status, priority });
            const tickets = res.data || res;
            const tbody = $("#support-list-body");
            if (!tbody.length) return;
            tbody.empty();

            if (!tickets || tickets.length === 0) {
                tbody.html('<tr><td colspan="7" class="text-center py-5 text-muted">No tickets found.</td></tr>');
                return;
            }

            tickets.forEach(ticket => {
                const statusBadge = support.getStatusBadge(ticket.status);
                const priorityBadge = support.getPriorityBadge(ticket.priority || "MEDIUM");
                tbody.append(`
                    <tr onclick="layout.render('Support/Admin', 'Details', '${ticket.id}')">
                        <td class="ps-4 fw-bold">#${ticket.id}</td>
                        <td>${ticket.customerName || '---'}</td>
                        <td class="text-truncate" style="max-width: 250px;">${ticket.title}</td>
                        <td>${statusBadge}</td>
                        <td>${priorityBadge}</td>
                        <td>${ticket.createdAt ? new Date(ticket.createdAt).toLocaleDateString('en-GB') : '---'}</td>
                        <td class="text-end pe-4">
                            <button class="btn btn-sm btn-light rounded-circle" onclick="layout.render('Support/Admin', 'Details', '${ticket.id}')">
                                <i class="icon icon-arrow-right"></i>
                            </button>
                        </td>
                    </tr>
                `);
            });
        } catch (e) {
            api.showToast("Error loading tickets: " + e.message, "error");
        }
    },

    /**
     * Load ticket details
     */
    loadTicketDetails: async (ticketId) => {
        try {
            const res = await api.get(`/support/${ticketId}`);
            const ticket = res.data;

            $("#ticket-detail-title").text(ticket.title);
            $("#ticket-detail-id").text("ID: #" + ticket.id);
            $("#ticket-detail-content").text(ticket.content);
            $("#ticket-customer-name").text(ticket.customerName);
            $("#ticket-customer-email").text(ticket.email);
            $("#ticket-created-date").text(new Date(ticket.createdAt).toLocaleString('en-GB'));
            
            const badge = $("#ticket-status-badge");
            badge.text(ticket.status);
            badge.removeClass().addClass(`badge bg-${support.getStatusClass(ticket.status)} rounded-pill px-3 py-2`);
            
            $("#ticket-status-select").val(ticket.status);
            $("#ticket-priority-select").val(ticket.priority || "MEDIUM");
            $("#ticket-category").text(ticket.category || "General");
            $("#ticket-updated-date").text(ticket.updatedAt ? new Date(ticket.updatedAt).toLocaleString('en-GB') : '---');
        } catch (e) {
            api.showToast("Error loading details: " + e.message, "error");
        }
    },

    /**
     * Add comment to ticket
     */
    addComment: async (event) => {
        event.preventDefault();
        const ticketId = layout.current.id;
        const commentText = $("#comment-text").val();
        if(!commentText) return;

        try {
            await api.post(`/support/${ticketId}/comment`, {
                content: commentText
            });

            api.showToast("Comment added!", "success");
            $("#comment-text").val("");
            support.loadTicketDetails(ticketId);
        } catch (e) {
            api.showToast("Error adding comment: " + e.message, "error");
        }
    },

    /**
     * Update ticket status
     */
    updateTicketStatus: async () => {
        const ticketId = layout.current.id;
        const status = $("#ticket-status-select").val();
        try {
            await api.put(`/support/${ticketId}`, { status: status });
            api.showToast("Status updated!", "success");
        } catch (e) {
            api.showToast("Update failed: " + e.message, "error");
        }
    },

    /**
     * Update ticket priority
     */
    updateTicketPriority: async () => {
        const ticketId = layout.current.id;
        const priority = $("#ticket-priority-select").val();
        try {
            await api.put(`/support/${ticketId}`, { priority: priority });
            api.showToast("Priority updated!", "success");
        } catch (e) {
            api.showToast("Update failed: " + e.message, "error");
        }
    },

    /**
     * Confirm delete ticket
     */
    confirmDelete: async () => {
        const ticketId = layout.current.id;
        if (!confirm("Delete this ticket?")) return;
        try {
            await api.delete(`/support/${ticketId}`);
            api.showToast("Ticket deleted!");
            layout.render('Support/Admin', 'Index');
        } catch (e) {
            api.showToast("Delete failed: " + e.message, "error");
        }
    },

    /**
     * Get status badge HTML
     */
    getStatusBadge: (status) => {
        const badges = {
            'OPEN': '<span class="badge bg-warning text-dark">Open</span>',
            'PROCESSING': '<span class="badge bg-info text-white">Processing</span>',
            'CLOSED': '<span class="badge bg-success">Closed</span>'
        };
        return badges[status] || `<span class="badge bg-secondary">${status}</span>`;
    },

    /**
     * Get status CSS class
     */
    getStatusClass: (status) => {
        const classes = {
            'OPEN': 'warning',
            'PROCESSING': 'info',
            'CLOSED': 'success'
        };
        return classes[status] || 'secondary';
    },

    /**
     * Get priority badge HTML
     */
    getPriorityBadge: (priority) => {
        const badges = {
            'LOW': '<span class="badge bg-light text-dark">Low</span>',
            'MEDIUM': '<span class="badge bg-warning text-dark">Medium</span>',
            'HIGH': '<span class="badge bg-danger">High</span>'
        };
        return badges[priority] || `<span class="badge bg-secondary">${priority}</span>`;
    }
};
