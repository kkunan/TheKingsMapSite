var list;
var iconBase = 'image/';

var textThai =
    'จุฬาฯ,13.738853,100.530538,จุฬาลงกรณ์มหาวิทยาลัย,king09.jpg'+'\n' +
    'สนามหลวง,13.754937,100.493058,สนามหลวง เนื้อหา,king01.jpg'+'\n' +
    'โรงพยาบาลเมาท์ออร์เบิร์น,42.37396,-71.13412,สถานที่พระราชสมภพ,king01.jpg'
    ;

var textEng =
    'ChulalongKorn University,13.738853,100.530538,CU,king09.jpg'+"\n"+
    'Sanamluang,13.754937,100.493058,Sanamluang description,king01.jpg';
list = textThai.split("\n");

        function initMap() {

          //  var scg = {lat: 13.918111, lng: 100.545001};
            var bounds = new google.maps.LatLngBounds();

            var infowindow = new google.maps.InfoWindow();
            var map = new google.maps.Map(document.getElementById('map'), {
          //      zoom: 10,
         //       center: scg,
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
      //          if (line != 0) {
                    var eachLocation = list[line].split(',');

                    var name = eachLocation[0];

                    var coordinate = {lat: parseFloat(eachLocation[1]), lng: parseFloat(eachLocation[2])};

                    var check = checkCookies(eachLocation[3]);
                    var contentString =
                            /* Title */
                            eachLocation[0]+'<br/>'+
                            /* Image */
                            '<IMG BORDER="0" ALIGN="Left" SRC="' + eachLocation[4] +' "><br/> ' +
                            /* Content */
                            eachLocation[3] +
                            /* CheckBox */
                            '<br/><input type="checkbox" ' +
                                'name="' + eachLocation[3] + '" ' +
                                'value="' + eachLocation[3] + '" ' +
                                'onclick=handleClick(this); ' +
                                 check + " >I've been here before</input><br/>"+
                            /* Next Button */
                            '<button>next</button><br/>'
                        ;

                    var marker = new google.maps.Marker({
                        icon: icons['firstMarker'],
                        position: coordinate,
                        map: map,
                        title: name
                    });

                    bounds.extend(marker.getPosition());

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
     //           }
            }
            map.fitBounds(bounds);
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



     //    function readTextFile(file) {
     //        var allText;
     //        var rawFile = new XMLHttpRequest();
     //
     //        var boolean = false;
     // //       do {
     //            rawFile.open("GET", file, true);
     //            rawFile.onreadystatechange = function () {
     //                if (rawFile.readyState === 4) {
     //                    if (rawFile.status === 200 || rawFile.status == 0) {
     //                        allText = rawFile.responseText;
     //                        list = allText.split("\n");
     //
     //                        boolean = true;
     //                    }
     //                }
     //            }
     //            rawFile.send(null);
     // //           alert(rawFile.readyState);
     // //       }while(!boolean);
     //        // var reader = new FileReader();
     //        //
     //        // reader.onload = function(e) {
     //        //     list = reader.result.split("\n");
     //        // }
     //        //
     //        // reader.readAsText(file, "utf-8");
     //
     //    }



     //   alert(document.cookie);
    //    readTextFile("content_thai.csv");
