export const isVersionDuplicate = (version, previousVersions) => {
    if (version) {
        if (previousVersions.includes(version.trim()) || previousVersions.includes("v" + version.trim())) {
            return true;
        } else {
            return false;
        }
    }
}