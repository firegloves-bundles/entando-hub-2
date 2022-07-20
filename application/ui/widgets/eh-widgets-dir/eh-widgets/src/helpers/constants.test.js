import {BUNDLE_URL_REGEX} from "./constants"

const testUrl = (value) => {
    return new RegExp(BUNDLE_URL_REGEX).test(value)
}

//Bad URLs/URIs
test('incomplete url', () => {
    expect(testUrl("some.url")).toBeFalsy();
})
test('missing url', () => {
    expect(testUrl(" ")).toBeFalsy();
})
test('non git/docker uri', () => {
    expect(testUrl("www.entando.com")).toBeFalsy();
})
test('not https', () => {
    expect(testUrl("http://github.com/account/reponame.git")).toBeFalsy();
})
test('incorrect docker path uri, e.g. includes /r/', () => {
    expect(testUrl("docker://registry.hub.docker.com/r/username/user-bundle")).toBeFalsy();
})

//Good URLs/URIs
test('v1 .git bundle url', () => {
    expect(testUrl("https://github.com/account/reponame.git")).toBeTruthy();
})
test('v1 git:// bundle url', () => {
    expect(testUrl("git@github.com:account/reponame.git")).toBeTruthy();
})
test('v5 docker:// bundle uri', () => {
    expect(testUrl("docker://registry.hub.docker.com/account/reponame")).toBeTruthy();
})
test('v5 docker:// bundle uri', () => {
    expect(testUrl("docker://registry.hub.docker.com/account/reponame")).toBeTruthy();
})


