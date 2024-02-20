import FingerprintJS from '@fingerprintjs/fingerprintjs';
import { fingerprintKey } from '@/assets/constants/blLocalstorage'

/**
 * Get the fingerprint of the user
 */
export const getFingerprint = () => {
  const fing = localStorage.getItem('fingerprint');
  if (fing != null) return;
  FingerprintJS.load().then((fp) => {
    fp.get().then((result) => {
      const visitorId = result.visitorId;
      localStorage.setItem(fingerprintKey, visitorId);
    });
  });
}
