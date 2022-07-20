import {clickableUrlFromUri} from "./helpers"

test('.git url', () => {
    expect(clickableUrlFromUri("https://github.com/account/reponame.git")).toBe("https://github.com/account/reponame.git");
})

test('git@ uri converted to URL', () => {
    expect(clickableUrlFromUri("git@github.com:account/reponame.git")).toBe("https://github.com/account/reponame.git");
})

test('random url is unchanged', () => {
    expect(clickableUrlFromUri("https://test.com")).toBe("https://test.com");
})

test('docker:// converted to URL', () => {
    expect(clickableUrlFromUri("docker://registry.hub.docker.com/account/reponame")).toBe("https://registry.hub.docker.com/r/account/reponame");
})



