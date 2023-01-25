import {clickableUrlFromUri, setBundleNameFromRepoAddress} from "./helpers"

//clickableUrlFromUri
test('(clickableUrlFromUri) .git url', () => {
    expect(clickableUrlFromUri("https://github.com/account/reponame.git")).toBe("https://github.com/account/reponame.git");
})

test('(clickableUrlFromUri) git@ uri converted to URL', () => {
    expect(clickableUrlFromUri("git@github.com:account/reponame.git")).toBe("https://github.com/account/reponame.git");
})

test('(clickableUrlFromUri) random url is unchanged', () => {
    expect(clickableUrlFromUri("https://test.com")).toBe("https://test.com");
})

test('(clickableUrlFromUri) docker:// converted to URL', () => {
    expect(clickableUrlFromUri("docker://registry.hub.docker.com/account/reponame")).toBe("https://registry.hub.docker.com/r/account/reponame");
})

//setBundleNameFromRepoAddress
test('(setBundleNameFromRepoAddress) https url', () => {
    expect(setBundleNameFromRepoAddress({gitRepoAddress:"https://github.com/account/reponame.git"})).toMatchObject({name:"reponame"});
})

test('(setBundleNameFromRepoAddress) git: url', () => {
    expect(setBundleNameFromRepoAddress({gitRepoAddress:"git@github.com:account/gitreponame.git"})).toMatchObject({name:"gitreponame"});
})

test('(setBundleNameFromRepoAddress) no repo address', () => {
    expect(setBundleNameFromRepoAddress({})).toMatchObject({name:""});
})

test('(setBundleNameFromRepoAddress) docker url', () => {
    expect(setBundleNameFromRepoAddress({gitRepoAddress:"docker://registry.hub.docker.com/account/dockerreponame"})).toMatchObject({name:"dockerreponame"});
})





