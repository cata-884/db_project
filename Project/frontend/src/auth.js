const KEY = 'movieapp_user';

export const auth = {
  save: (user) => localStorage.setItem(KEY, JSON.stringify(user)),
  get: () => {
    const data = localStorage.getItem(KEY);
    return data ? JSON.parse(data) : null;
  },
  clear: () => localStorage.removeItem(KEY),
  isLoggedIn: () => localStorage.getItem(KEY) !== null
};

