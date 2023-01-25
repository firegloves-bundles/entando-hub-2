export const isVersionDuplicate = (version, previousVersions) => {
    if (version) {
        const trimmed = version.trim();
        return !!(previousVersions.includes(trimmed) || previousVersions.includes("v" + trimmed));
    }
}