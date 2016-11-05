var list;
        var iconBase = 'image/';

        function initMap() {

            var scg = {lat: 13.918111, lng: 100.545001};
            var infowindow = new google.maps.InfoWindow();
            var map = new google.maps.Map(document.getElementById('map'), {
                zoom: 10,
                center: scg,
                mapTypeControl: false
            });

            var icons = {
                firstMarker : {
                url: iconBase + "marker.png", // url
              //  scaledSize: new google.maps.Size(25, 25), // scaled size
                origin: new google.maps.Point(0, 0), // origin
                anchor: new google.maps.Point(0, 0) // anchor
            }
        };
            for (line in list) {
                if (line != 0) {
                    var eachLocation = list[line].split(',');

                    var name = eachLocation[0];

                    var coordinate = {lat: parseFloat(eachLocation[1]), lng: parseFloat(eachLocation[2])};

                    var check = checkCookies(eachLocation[3]);
                    var contentString = "<IMG BORDER=\"0\" ALIGN=\"Left\" SRC=\"" + eachLocation[4] + "\"> " + eachLocation[3]
                            + "<br/><input type=\"checkbox\" " +
                            "name=\"" + eachLocation[3] + "\" " +
                            "value=\"" + eachLocation[3] + "\" " +
                            "onclick='handleClick(this);' " +
                            check + " >I've been here before</input><br/>";

                    var marker = new google.maps.Marker({
                        icon: icons['firstMarker'],
                        position: coordinate,
                        map: map,
                        title: name
                    });

                    google.maps.event.addListener(marker, 'click', (function (marker, content, infowindow) {
                        return function () {
                            infowindow.setContent(content)
                            infowindow.open(map, marker);
                        };
                    })(marker, contentString, infowindow));

                    function checkCookies(name) {

                        if (getCookie(name) == "true") {
                            //                 alert(name+" "+getCookie(name));
                            return "checked";
                        }
                        else return "";
                    }
                }
            }

        }

        function handleClick(cb) {
            setCookie(cb.name, cb.checked)
            //     alert(getCookie(cb.name));
        }

        function setCookie(cname, cvalue) {
            document.cookie = cname + "=" + cvalue + ";path=/";
        }

        function getCookie(cname) {
            var name = cname + "=";
            var ca = document.cookie.split(';');
            for (var i = 0; i < ca.length; i++) {
                var c = ca[i];
                while (c.charAt(0) == ' ') {
                    c = c.substring(1);
                }
                if (c.indexOf(name) == 0) {
                    return c.substring(name.length, c.length);
                }
            }
            return "";
        }

        function readTextFile(file) {
            var allText;
            var rawFile = new XMLHttpRequest();

            rawFile.open("GET", file, true);
            rawFile.onreadystatechange = function () {
                if (rawFile.readyState === 4) {
                    if (rawFile.status === 200 || rawFile.status == 0) {
                        allText = rawFile.responseText;
                        list = allText.split("\n");

                    }
                }
            }
            rawFile.send(null);
        }
     //   alert(document.cookie);
        readTextFile("content_thai.csv");