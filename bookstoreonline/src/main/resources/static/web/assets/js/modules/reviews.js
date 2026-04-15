/**
 * reviews.js - Book Reviews & Ratings
 */
const reviews = {
    loadList: async (isbn) => {
        try {
            const res = await api.get(`/reviews/book/${isbn}`);
            const list = res.data || [];
            const container = $("#reviews-container");
            if (!container.length) return;
            container.empty();

            if (list.length === 0) {
                container.html('<p class="text-muted">No reviews yet. Be the first to share your thoughts!</p>');
                return;
            }

            list.forEach(r => {
                container.append(`
                    <div class="review-item mb-4 pb-3 border-bottom">
                        <div class="d-flex justify-content-between mb-2">
                            <span class="fw-bold text-dark">${r.username}</span>
                            <span class="text-muted small">${common.formatDate(r.createdAt)}</span>
                        </div>
                        <div class="rating-stars text-warning mb-2">
                            ${Array(r.rating).fill('★').join('')}${Array(5-r.rating).fill('☆').join('')}
                        </div>
                        <p class="review-comment mb-0">${r.comment}</p>
                    </div>
                `);
            });
        } catch (e) {
            console.error("Reviews load error", e);
        }
    },

    submit: async (isbn) => {
        const rating = $("input[name='review-rating']:checked").val();
        const comment = $("#review-comment-input").val().trim();

        if (!rating) { api.showToast("Please select a rating", "warning"); return; }
        if (!comment) { api.showToast("Please write a comment", "warning"); return; }

        try {
            await api.post('/reviews', { isbn, rating: parseInt(rating), comment });
            api.showToast("Thank you for your review!");
            $("#review-comment-input").val('');
            reviews.loadList(isbn);
        } catch (e) {
            api.showToast("Failed to submit review: " + e.message, "error");
        }
    }
};
