/**
 * publishers.js - Simple fetch module for publishers logic
 */
const publishers = {
    getAll: async () => {
         try {
             const res = await api.get('/publishers');
             return Array.isArray(res) ? res : (res.data || []);
         } catch(e) {
             console.error("Publishers fetch error", e);
             return [];
         }
    }
};
