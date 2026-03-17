chrome.runtime.onInstalled.addListener(() => {
  const extensionUrl = chrome.runtime.getURL('/build/dist/js/developmentExecutable/index.html');

  const rules = [
    {
      id: 1,
      priority: 1,
      action: {
        type: 'redirect',
        redirect: {
          regexSubstitution: `${extensionUrl}?APPNAME=CampusNet&PRGNAME=STARTPAGE_DISPATCH&\\1`
        }
      },
      condition: {
        regexFilter: "^https://www\\.tucan\\.tu-darmstadt\\.de/scripts/mgrqispi\\.dll\\?APPNAME=CampusNet&PRGNAME=STARTPAGE_DISPATCH&(.*)",
        resourceTypes: ['main_frame']
      }
    }
  ];

  chrome.declarativeNetRequest.updateDynamicRules({
    removeRuleIds: [1],
    addRules: rules
  }, () => {
    if (chrome.runtime.lastError) {
      console.error("DNR Rule Error:", chrome.runtime.lastError.message);
    } else {
      console.log("Redirect rule with query preservation active.");
    }
  });
});