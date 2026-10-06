/**
 * Конфигурация Firebase (вариант 8 — Автобаза).
 * Проект: lab2part3-31c63 (Firebase Console → Project settings → Your apps)
 */
export const firebaseConfig = {
  apiKey: 'AIzaSyAxzDw_7H-s15CnG0bVJjFTHAUIewiEWJI',
  authDomain: 'lab2part3-31c63.firebaseapp.com',
  projectId: 'lab2part3-31c63',
  storageBucket: 'lab2part3-31c63.firebasestorage.app',
  messagingSenderId: '498338749344',
  appId: '1:498338749344:web:2acbd026b7396dfa9ee2c5'
};

export const FIRESTORE_COLLECTION = 'trips';

export function isFirebaseConfigured(): boolean {
  return Boolean(
    firebaseConfig.apiKey &&
      firebaseConfig.projectId &&
      firebaseConfig.appId
  );
}
