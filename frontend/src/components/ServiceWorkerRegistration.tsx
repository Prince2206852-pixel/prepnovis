"use client";

import { useEffect } from "react";

export default function ServiceWorkerRegistration() {
  useEffect(() => {
    if (!("serviceWorker" in navigator)) {
      return;
    }

    const registerServiceWorker = async () => {
      try {
        const registration = await navigator.serviceWorker.register("/sw.js", {
          scope: "/",
        });

        console.log(
          "PrepNovis service worker registered:",
          registration.scope
        );
      } catch (error) {
        console.error(
          "PrepNovis service worker registration failed:",
          error
        );
      }
    };

    registerServiceWorker();
  }, []);

  return null;
}