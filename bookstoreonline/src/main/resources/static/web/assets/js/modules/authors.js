/**
 * authors.js - Simple fetch module for authors logic
 */
const authors = {
    getAll: async () => {
         try {
             const res = await api.get('/authors');
             return Array.isArray(res) ? res : (res.data || []);
         } catch(e) {
             console.error("Authors fetch error", e);
             return [];
         }
    }
};
