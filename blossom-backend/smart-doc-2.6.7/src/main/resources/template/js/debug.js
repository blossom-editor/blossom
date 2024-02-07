$(function () {
    const Accordion = function (el, multiple) {
        this.el = el || {};
        this.multiple = multiple || false;
        const links = this.el.find('.dd');
        links.on('click', {el: this.el, multiple: this.multiple}, this.dropdown);
    };
    Accordion.prototype.dropdown = function (e) {
        const $el = e.data.el;
        const $this = $(this), $next = $this.next();
        $next.slideToggle();
        $this.parent().toggleClass('open');
        if (!e.data.multiple) {
            $el.find('.submenu').not($next).slideUp("20").parent().removeClass(
                'open');
        }
    };
    new Accordion($('#accordion'), false);
    hljs.highlightAll();
});

$("[contenteditable=plaintext-only]").on('blur', function (e) {
    e.preventDefault();
    const $this = $(this);
    const data = $this.text();
    let content;
    if (undefined === e.originalEvent.clipboardData) {
        content = data;
    } else {
        content = e.originalEvent.clipboardData.getData('text/plain')
    }
    content = JSON.stringify(JSON.parse(content), null, 4);
    const highlightedCode = hljs.highlight('json', content).value;
    $this.html(highlightedCode);
})
$("button").on("click", function () {
    const $this = $(this);
    const id = $this.data("id");
    console.log("method-id=>" + id);

    let body = $("#" + id + "-body").text();

    // header
    const $headerElement = $("#" + id + "-header");
    const headersData = getInputData($headerElement);

    // body param
    const $paramElement = $("#" + id + "-param");
    let bodyParamData = getInputData($paramElement)

    // path param
    const $pathElement = $("#" + id + "-path-params")
    const pathParamData = getInputData($pathElement)

    // query param
    const $queryElement = $("#" + id + "-query-params")
    let $urlDataElement = $("#" + id + "-url");
    const url = $urlDataElement.data("url");
    const isDownload = $urlDataElement.data("download");
    const page = $urlDataElement.data("page");
    const method = $("#" + id + "-method").data("method");
    const contentType = $("#" + id + "-content-type").data("content-type");
    console.log("request-headers=>" + JSON.stringify(headersData))
    console.log("path-params=>" + JSON.stringify(pathParamData))

    console.log("body-params=>" + JSON.stringify(bodyParamData))
    console.log("json-body=>" + body);
    let finalUrl = "";
    let queryParamData;
    if (!isEmpty(page)) {
        queryParamData = getInputData($queryElement)
        finalUrl = castToGetUri(page, pathParamData, queryParamData)
        window.open(finalUrl, "_blank");
        return;
    }
    if (isDownload) {
        queryParamData = getInputData($queryElement);
        download(url, headersData, pathParamData, queryParamData, bodyParamData,
            method, contentType);
        return;
    }
    const ajaxOptions = {};

    if ("multipart/form-data" === contentType) {
        finalUrl = castToGetUri(url, pathParamData);
        queryParamData = getInputData($queryElement, true,true)
        body = queryParamData;
        ajaxOptions.processData = false;
        ajaxOptions.contentType = false;
    } else if ("POST" === method && contentType !== "multipart/form-data"
        && contentType !== "application/json") {
        finalUrl = castToGetUri(url, pathParamData);
        queryParamData = getInputData($queryElement,true)
        body = queryParamData;
    } else {
        queryParamData = getInputData($queryElement)
        finalUrl = castToGetUri(url, pathParamData, queryParamData)
        ajaxOptions.contentType = contentType;
    }
    console.log("query-params=>" + JSON.stringify(queryParamData));
    console.log("url=>" + finalUrl)
    ajaxOptions.headers = headersData
    ajaxOptions.url = finalUrl
    ajaxOptions.type = method
    ajaxOptions.data = body;

    const $responseEle = $("#" + id + "-response").find("pre code");
    const ajaxTime = new Date().getTime();
    $.ajax(ajaxOptions).done(function (result, textStatus, jqXHR) {
        const totalTime = new Date().getTime() - ajaxTime;
        $this.css("background", "#5cb85c");
        $("#" + id + "-resp-status").html(
            "&nbsp;Status:&nbsp;" + jqXHR.status + "&nbsp;&nbsp;" + jqXHR.statusText
            + "&nbsp;&nbsp;&nbsp;&nbsp;Time:&nbsp;" + totalTime + "&nbsp;ms");
        const highlightedCode = hljs.highlight('json',
            JSON.stringify(result, null, 4)).value;
        $responseEle.html(highlightedCode);
    }).fail(function (jqXHR) {
        const totalTime = new Date().getTime() - ajaxTime;
        $this.css("background", "#D44B47");
        if (jqXHR.status === 0 && jqXHR.readyState === 0) {
            $("#" + id + "-resp-status").html(
                "Connection refused, please check the server.");
        } else {
            $("#" + id + "-resp-status").html(
                "&nbsp;Status:&nbsp;" + jqXHR.status + "&nbsp;&nbsp;"
                + jqXHR.statusText + "&nbsp;&nbsp;&nbsp;&nbsp;Time:&nbsp;" + totalTime
                + "&nbsp;ms");
        }
        if (undefined !== jqXHR.responseJSON) {
            const highlightedCode = hljs.highlight('json',
                JSON.stringify(jqXHR.responseJSON, null, 4)).value;
            $responseEle.html(highlightedCode);
        }
    }).always(function () {

    });
    const curlCmd = toCurl(ajaxOptions);
    const highlightedCode = hljs.highlight('bash', curlCmd).value;
    $("#" + id + "-curl").find("pre code").html(highlightedCode);
})
$(".check-all").on("click", function () {
    const checkboxName = $(this).prop("name");
    const checked = $(this).is(':checked');
    if (!checked) {
        $(this).removeAttr("checked");
    } else {
        $(this).prop("checked", true);
    }
    $('input[name="' + checkboxName + '"]').each(function () {
        if (!checked) {
            $(this).prop("checked", false);
        } else {
            $(this).prop("checked", true);
        }
    })
})

function castToGetUri(url, pathParams, params) {
    if (pathParams instanceof Object && !(pathParams instanceof Array)) {
        url = url.format(pathParams)
    }
    if (params instanceof Object && !(params instanceof Array)) {
        const pm = params || {};
        const arr = [];
        arr.push(url);
        let j = 0;
        for (const i in pm) {
            if (j === 0) {
                arr.push("?");
                arr.push(i + "=" + pm[i]);
            } else {
                arr.push("&" + i + "=" + pm[i]);
            }
            j++;
        }
        return arr.join("");
    } else {
        return url;
    }
}

function getInputData(element, isPost, returnFormDate) {
    const formData = new FormData();
    $(element).find("tr").each(function (i) {
        const checked = $(this).find('td:eq(0)').children(".checkbox").children(
            "input").is(':checked');
        if (checked) {
            const input = $(this).find('td:eq(2) input');
            let attr = $(input).attr("type");
            let multiple = $(input).attr("multiple");
            console.log("input type:" + attr)
            const name = $(input).attr("name");
            if (attr === "file") {
                let files = $(input)[0].files;
                if (multiple) {
                    $.each(files, function (i, file) {
                        formData.append(name, file);
                    })
                } else {
                    formData.append(name, files[0]);
                }
            } else {
                const val = $(input).val();
                if (isValidUrl(val)) {
                    formData.append(name, encodeURI(val));
                } else {
                    // support chinese
                    if (hasChinese(val) && !isPost) {
                        formData.append(name, encodeURIComponent(val));
                    } else {
                        formData.append(name, val);
                    }
                }
            }
        }
    });
    if (returnFormDate) {
        return formData;
    }
    const headersData = {};
    formData.forEach((value, key) => headersData[key] = value);
    return headersData;
}

String.prototype.format = function (args) {
    let reg;
    if (arguments.length > 0) {
        let result = this;
        if (arguments.length === 1 && typeof (args) == "object") {
            for (const key in args) {
                reg = new RegExp("({" + key + "})", "g");
                result = result.replace(reg, args[key]);
            }
        } else {
            for (let i = 0; i < arguments.length; i++) {
                if (arguments[i] === undefined) {
                    return "";
                } else {
                    reg = new RegExp("({[" + i + "]})", "g");
                    result = result.replace(reg, arguments[i]);
                }
            }
        }
        return result;
    } else {
        return this;
    }
}

function download(url, headersData, pathParamData, queryParamData,
                  bodyParamData, method, contentType) {
    url = castToGetUri(url, pathParamData, queryParamData)
    const xmlRequest = new XMLHttpRequest();
    xmlRequest.open(method, url, true);
    xmlRequest.setRequestHeader("Content-type", contentType);
    for (let key in headersData) {
        xmlRequest.setRequestHeader(key, headersData[key])
    }
    xmlRequest.responseType = "blob";
    xmlRequest.onload = function () {
        if (this.status === 200) {
            let fileName = "";
            const disposition = xmlRequest.getResponseHeader('Content-Disposition');
            if (disposition && disposition.indexOf('attachment') !== -1) {
                const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                const matches = filenameRegex.exec(disposition);
                if (matches != null && matches[1]) {
                    fileName = decodeURI(matches[1].replace(/['"]/g, ''));
                }
            }
            console.log("download filename:" + fileName);
            const blob = this.response;
            if (navigator.msSaveBlob) {
                // IE10 can't do a[download], only Blobs:
                window.navigator.msSaveBlob(blob, fileName);
                return;
            }
            if (window.URL) { // simple fast and modern way using Blob and URL:
                const a = document.createElement("a");
                document.body.appendChild(a);
                const url = window.URL.createObjectURL(blob);
                a.href = url;
                a.download = fileName;
                a.click();
                window.URL.revokeObjectURL(url);
            }
            console.log(fileName);
        } else {
            console.log("download failed");
        }
    };
    try {
        xmlRequest.send(bodyParamData);
    } catch (e) {
        console.error("Failed to send data", e);
    }
}

function toCurl(request) {
    if (typeof request !== 'object') {
        throw "Request is not an object";
    }
    // default is a GET request
    const cmd = ["curl", "-X", request.type || "GET"];

    if (request.url.indexOf('https') === 0) {
        cmd.push("-k");
    }

    // append Content-Type
    if (request.contentType) {
        cmd.push("-H");
        cmd.push("'Content-Type:");
        cmd.push(request.contentType + "'");
    }
    // append request headers
    let headerValue;
    if (typeof request.headers == 'object') {
        for (let key in request.headers) {
            if (Object.prototype.hasOwnProperty.call(request.headers, key)) {
                cmd.push("-H");
                headerValue = request.headers[key];
                if (headerValue.value === '') {
                    cmd.push("'" + key + "'");
                } else {
                    cmd.push("'" + key + ':' + headerValue + "'");
                }
            }
        }
    }

    // display the response headers
    cmd.push("-i");
    // append request url
    let url = request.url;
    if (!url.startsWith("http")) {
        const protocol = window.document.location.protocol;
        const domain = window.document.location.hostname;
        const port = window.document.location.port;
        url = protocol + "//" + domain + ":" + port + url;
    }
    cmd.push(url);
    // append data

    if (typeof request.data == 'object') {
        let index = 0;
        const bodyData = [];
        bodyData.push("\"")
        for (let key in request.data) {
            if (Object.prototype.hasOwnProperty.call(request.data, key)) {
                if (index === 0) {
                    bodyData.push(key);
                    bodyData.push("=");
                    bodyData.push(request.data[key]);
                } else {
                    bodyData.push("&")
                    bodyData.push(key);
                    bodyData.push("=");
                    bodyData.push(request.data[key]);
                }
            }
            index++;
        }
        bodyData.push("\"");
        let bodyStr = ""
        bodyData.forEach(function (item) {
            bodyStr += item;
        });
        cmd.push("--data");
        cmd.push(bodyStr);
    } else if (request.data && request.data.length > 0) {
        // append json data
        cmd.push("--data");
        cmd.push("'" + request.data + "'");
    }

    let curlCmd = "";
    cmd.forEach(function (item) {
        curlCmd += item + " ";
    });
    console.log(curlCmd);
    return curlCmd;
}

function isEmpty(obj) {
    return obj === undefined || obj === null || String(obj).trim() === '';
}

function hasChinese(_string) {
    const chineseReg = /[\u4e00-\u9fa5]/g
    return chineseReg.test(_string);
}

function isValidUrl(_string) {
    let urlString;
    try {
        urlString = new URL(_string);
    } catch (_) {
        return false;
    }
    return urlString.protocol === "http:" || urlString.protocol === "https:";
}
