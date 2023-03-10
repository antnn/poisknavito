(async function () {
        function getElementByXpath(path) {
                return document.evaluate(path, document,
                        null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
        }
        function isLoggedIn() {
                return localStorage.getItem('___LOGGED');
        }
        function timeout(ms) {
                return new Promise(resolve => setTimeout(resolve, ms));
        }



        async function LogIn() {
                var loop_ = true;
                var waitAmount = 1000;
                var login_btn_clicked = () => {
                        return window.localStorage.getItem("login_btn_clicked");
                };
                if (isLoggedIn())
                        return;
                while (loop_) {
                        try {
                                if (!login_btn_clicked()) {
                                        await timeout(waitAmount);
                                        var login_btn = getElementByXpath(' //*[@id="modal"]/div/div/div/div[2]');
                                        login_btn.click();
                                        window.localStorage.setItem("login_btn_clicked", true);
                                }

                                await timeout(waitAmount);
                                var inp = getElementByXpath('//*[@id="modal"]/div/div/div[2]/form/div[1]/div/div/div/div/div/input');
                                inp.focus();
                                inp.click();
                                AndroidInput.sendTextInput("790012345678");
                                await timeout(waitAmount);
                                var inpp = getElementByXpath('//*[@id="modal"]/div/div/div[2]/form/div[2]/div/div/div/div/div[1]/input');
                                inpp.focus();
                                inpp.click();
                                AndroidInput.sendTextInput("PASSWORD!");
                                await timeout(waitAmount);
                                var form_button = getElementByXpath('//*[@id="modal"]/div/div/div[2]/div/div[1]/span');
                                form_button.click();
                                localStorage.setItem('___LOGGED', true);
                                await timeout(2*waitAmount); /*if not redirected by click on form - error*/
                                localStorage.setItem('___LOGGED', false);
                                loop_ = false;
                        } catch {
                                continue;
                        }
                }
        }
        await LogIn();
})()
