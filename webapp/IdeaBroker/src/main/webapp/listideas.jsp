<!-- STRUTS -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>
<!-- END STRUTS -->

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <link href="bootstrap-3.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/login.css" rel="stylesheet">
    <link href="css/clouds.css" rel="stylesheet">
    <link href="css/simple-sidebar.css" rel="stylesheet">
    <link href="css/banner.css" rel="stylesheet">
    <link href="css/bootstrap-dialog.css" rel="stylesheet">
    <!--<link href="css/3dbtn.css" rel="stylesheet">-->
    <title></title>
    <script src="jquery.js"></script>
    <script type="text/javascript" src="noty/jquery.noty.js"></script>

    <script type="text/javascript" src="noty/layouts/top.js"></script>
    <script type="text/javascript" src="noty/layouts/topRight.js"></script>
    <script type="text/javascript" src="noty/themes/default.js"></script>
    <script type="text/javascript">

        /**
         * WEBSOCKETS
         */
        window.onload = function() { // execute once the page loads
            initializeWebSockets();
        }

        function initializeWebSockets() { // URI = ws://10.16.0.165:8080/chat/chat
            connect('ws://' + window.location.host + '/chat');
        }

        function connect(host) { // connect to the host websocket servlet
            if ('WebSocket' in window)
                websocket = new WebSocket(host);
            else if ('MozWebSocket' in window)
                websocket = new MozWebSocket(host);
            else {
                //FIXME: SHIT
                return;
            }

            websocket.onopen    = onOpen; // set the event listeners below
            websocket.onclose   = onClose;
            websocket.onmessage = onMessage;
            websocket.onerror   = onError;
        }
        function onOpen(event) {
            console.log('Connected to ' + window.location.host + '.');
            makeNotification("Hello World!", "A Mariana � linda!!!!")
        }

        function makeNotification(title, body) {
            noty(
                    {   text:
                            '<div><h4 style="text-align:center; color:#ffcd41; text-shadow: 2px 2px 0px black;"><b>'+title+'<hr style="margin-top:10px;"/></b></h4></div><div>'+body+'</div>',
                        layout: 'topRight',
                        timeout: 5000,
                        type: 'information'
                    }
            );
        }

        function onClose(event) {
            //FIXME: CLosed, shit
            console.log("Websocket closed!");
        }

        function onMessage(message) { // print the received message
            console.log(message);
            console.log(message.data);
            not = $.parseJSON(message.data);

            //FIXME idea name

            if ( not.type == "TAKEOVER") {
                makeNotification("Idea Taken Over!",
                    "Your shares for idea "+not.iid+" have been taken over by the root user at the market value of "+not.marketValue+ " for a total of " + not.total+" DEICoins!");
           } else if ( not.type == "MARKETVALUE") {
                console.log("New marketvalue!");
                updateMarketValue(not.iid, not.marketValue);
            } else {

                numShares = not.numShares;
                pricePerShareTransaction = not.pricePerShare;
                username=not.username;
                iid=not.iid;
                currentShares=not.currentShares;
                money=not.money;
                currPricePerShare=not.currPricePerShare;
                totalInvolved = numShares*pricePerShareTransaction;
                // idea name MISSING FIXME
                if ( not.type == "BOUGHT" ) {
                    makeNotification("Bought Shares", "You have acquired "+numShares+" shares for idea "+iid
                                     +" at "+pricePerShareTransaction+" DEICoins each, for a total of "+totalInvolved
                                     +" DEICoins from user "+username);

                    console.log("We just bought "+numShares+" at "+pricePerShareTransaction+ " for a total of "+totalInvolved+" from "+username+" for idea "+iid);
                } else {
                    makeNotification("Sold Shares", "You have sold "+numShares+" shares from idea "+iid
                            +" at "+pricePerShareTransaction+" DEICoins each, for a total of "+totalInvolved
                            +" DEICoins to user "+username);
                    console.log("We just sold "+numShares+" at "+pricePerShareTransaction+ " for a total of "+totalInvolved+" to "+username+" for idea "+iid);
                }

                if ( haveIdeaOnWebpage(iid) ) {
                    setNumSharesForidea(iid, currentShares);
                    setSellingPriceIdea(iid, currPricePerShare);
                }
                setUserMoney(money);

                console.log("We currently have "+currentShares+" valuated at "+currPricePerShare+" each. And we have "+money+" DEICoins.");
            }
        }

        function onError(event) {
            console.log('WebSocket error (' + event.data + ').');
        }

        /**
         * MAXI STUFF
         */


        function getWatchListLabelStr(id) {
            return '#watchlistlabel'+id;
        }

        function getAddToWatchListButtonStr(id) {
            return '#addtowatchlistbtn'+id;
        }

        function getSetSharePriceBtnStr(id) {
            return '#btnsellingprice'+id;
        }

        function getSetSharePriceTextEditStr(id) {
            return '#sellingprice'+id;
        }

        function getNumSharesIdeaStr(id) {
            return '#numshares'+id;
        }

        function getPercentSharesIdeaStr(id) {
            return "#percentshares"+id;
        }

        function getShareNumLabelStr(id) {
            return "#ownidea"+id;
        }

        function getSharePriceEditBoxStr(id) {
            return '#setsharepriceeditbox'+id;
        }

        function getRemoveIdeaBtnStr(id) {
            return '#removeidea'+id;
        }

        function getIdeaStr(id) {
            return '#idea'+id;
        }

        function getMarketValueStr(id) {
            return '#marketvalue'+id;
        }

        function updateMarketValue(id, val) {
            if ( haveIdeaOnWebpage(id) ) {
                console.log("Printing new value: "+val);
                $(getMarketValueStr(id)).text(val);
            }
        }

        function haveIdeaOnWebpage(id) {
            return $(getIdeaStr(id)).length != 0;
        }

        function showShareNumLabel(id) {
            $(getShareNumLabelStr(id)).show();
        }

        function hideShareNumLabel(id) {
            $(getShareNumLabelStr(id)).hide();
        }


        function showSetSellingPriceBox(id) {
            $(getSharePriceEditBoxStr(id)).show();
        }

        function hideSetSellingPriceBox(id) {
            $(getSharePriceEditBoxStr(id)).hide();
        }

        function showRemoveIdeaBtn(id) {
            $(getRemoveIdeaBtnStr(id)).show();
        }

        function hideRemoveIdeaBtn(id) {
            $(getRemoveIdeaBtnStr(id)).hide();
        }

        function postJSON(page, data, func) {
            $.post(page, data, func, 'json');
        }

        function takeover(id) {
            var formData = {iid:id};
            postJSON('takeover.action', formData, function(data) {
                console.log(data);
                if ( data.success ) {
                    $(getIdeaStr(id)).hide();
                } else {
                    alert("Server Internal Error...RMI is probably down!");
                }
            })
        }

        function addToWatchlist(id){
            var formData = {iid:id}; //Array
            postJSON('addtowatchlist.action', formData,function(data) {

                if ( data.success ) {
                    $(getWatchListLabelStr(id)).show();
                    $(getAddToWatchListButtonStr(id)).hide();
                } else {
                    alert("Server Internal Error...RMI is probably down!");
                }
                return true;
            });
        }

        function removeFromWatchlist(id) {
            var formData = {iid:id}; //Array
            postJSON('removefromwatchlist.action', formData,function(data) {

                if ( data.success ) {
                    $(getAddToWatchListButtonStr(id)).show();
                    $(getWatchListLabelStr(id)).hide();
                } else {
                    alert("Server Internal Error...RMI is probably down!");
                }
                return true;
            });
        }

        function isValidPositiveNum(input) {
            return !isNaN(input) && parseFloat(input) > 0;
        }

        function isValidPositiveInt(input) {
            return !isNaN(input) && parseInt(input) > 0;
        }

        function sharePriceChanged(id) {
            text = $(getSetSharePriceTextEditStr(id));
            button = $(getSetSharePriceBtnStr(id));

            if ( isValidPositiveNum(text.val()) ) {
                text.removeAttr('title');
                text.removeAttr('title');
                button.addClass('btn-info').removeClass('btn-success').removeClass('btn-error');
                button.html('<span class="glyphicon glyphicon-share-alt"></span>');
                button.attr('title', 'Confirmar Altera��o de Pre�o');
                button.off('click').on('click',{id:id},setSharePrice);
                text.css('background-color','skyblue');
                text.css('color','black');
            } else {
                text.attr('title', 'Pre�o inv�lido!');
                button.attr('title', 'Pre�o inv�lido!');
                button.addClass('btn-danger').removeClass('btn-success').removeClass('btn-info');
                button.html('<span class="glyphicon glyphicon-remove"></span>');
                text.css('background-color','#d2322d');
                text.css('color','white');
            }
        }

        function setSharePrice(event) {
            id = event.data.id;
            text = $(getSetSharePriceTextEditStr(id));
            button = $(getSetSharePriceBtnStr(id));

            var formData = {iid:id,price:text.val()}; //Array
            postJSON('setshareprice.action', formData,function(data) {
                if ( data.success ) {
                    button.addClass('btn-success').removeClass('btn-info').removeClass('btn-error');;
                    button.html('<span class="glyphicon glyphicon-ok-sign"></span>');
                    button.removeAttr('title');
                    text.removeAttr('title');
                    button.off('click');
                    text.css('background-color','white');
                    text.css('color','black');
                } else {
                    alert("Server Internal Error...RMI is probably down!");
                }
                return true;
            });
        }

        function getUserMoney() {
            return parseFloat($('#currmoney').text());
        }

        function setUserMoney(money) {
            $('#currmoney').text(money);
        }

        function getMaxSharesForIdea(id) {
            return 100000;
        }

        function getNumSharesForIdea(id) {
            var numshareslabel = $(getNumSharesIdeaStr(id));
            if ( numshareslabel.length != 0)
                return numshareslabel.text();
            else
                return 0;
        }


        function setSellingPriceIdea(id, price) {
            $(getSetSharePriceTextEditStr(id)).val(price);
        }

        function getSellingPriceIdea(id, price) {
            if ( $(getSetSharePriceTextEditStr(id)).is(":visible") )
                return $(getSetSharePriceTextEditStr(id)).val();
            return -1;
        }

        function showMessage(title, message, type, callback) {
            new BootstrapDialog({
                message: message,
                type: type,
                title: title,
                data: {
                    'callback': callback
                },
                closable: false,
                buttons: [{
                    label: 'OK',
                    action: function(dialog) {
                        typeof dialog.getData('callback') === 'function' && dialog.getData('callback')(true);
                        dialog.close();
                    }
                }]
            }).open();
        }

        ////
        // CALL THIS FUNCTION TO UPDATE THE SHARES FOR AN IDEA. IT UPDATES ALL THE FIELDS IT HAS TOO. ALSO DON'T FORGET
        // THAT THIS SHOWS THE SET SELLING PRICE FIELD, SO YOU MIGHT WANT TO USE setSellingPriceIdea() TO UPDATE THAT!
        //
        function setNumSharesForidea(id, num) {
            /** FIXME: When we show, we need to get the selling price */
            var numshareslabel = $(getNumSharesIdeaStr(id));
            var percentshareslabel = $(getPercentSharesIdeaStr(id));
            var pct =  num / getMaxSharesForIdea() * 100.0;
            percentshareslabel.text(pct);
            numshareslabel.text(num);
            if ( num > 0 ) {
                showSetSellingPriceBox(id);
                showShareNumLabel(id);
                if ( pct == 100.0 )
                    showRemoveIdeaBtn(id);
                else
                    hideRemoveIdeaBtn(id);
            } else {
                hideSetSellingPriceBox(id);
                hideShareNumLabel(id);
                hideRemoveIdeaBtn(id);
            }
        }

        gClosedialog = null;

        function onMaxWillingToBuyChanged() {
            var m = $('#maxpershareinput');
            var modalsubmitbutton = $('#modalsubmitbutton');
            if (m.val() == '' || m.val() == 0.0 || !isValidPositiveNum(m.val())) {
                modalsubmitbutton.prop('disabled', true);
                return;
            }

            var maxwillingtobuy = parseFloat(m.val());
            var currentmoney = getUserMoney();
            //console.log('max: '+maxwillingtobuy+', money: '+currentmoney);



            if ( maxwillingtobuy > currentmoney ) {
                //Can't sell
                m.val(currentmoney);
            } else {
                //Can sell
                modalsubmitbutton.prop('disabled', false);
            }
        }

        function onNumSharesWantChanged(id) {
            var maxSharesAvail=getMaxSharesForIdea(id);
            var m = $('#numshareswant');
            var modalsubmitbutton = $('#modalsubmitbutton');
            if (m.val() == '' || m.val() == 0 || !isValidPositiveInt(m.val())) {
                modalsubmitbutton.prop('disabled', true);
                return;
            }

            var currentSharesWant = parseInt(m.val());


            if ( maxSharesAvail < currentSharesWant ) {
                //Can't sell
                m.val(maxSharesAvail);
            } else {
                //Can sell
                modalsubmitbutton.prop('disabled', false);
            }
        }

        function removeIdea(id) {
            var formData = {iid:id}; //Array
            postJSON('removeidea.action', formData,function(data) {
                if ( data.success ) {
                    if ( data.result == "OK" ) {
                        $(getIdeaStr(id)).hide();
                    } else {
                        showMessage('Erro', 'N�o foi poss�vel apgar a ideia porque n�o det�m 100% das suas shares.',
                                BootstrapDialog.TYPE_DANGER);
                    }
                } else {
                    alert("Server Internal Error...RMI is probably down!");
                }
                return true;
            });
        }

        function doBuyShares(id) {
            var maxPerShare     = parseFloat($('#maxpershareinput').val());
            var numSharesWant   = parseInt($('#numshareswant').val());
            var wantToQueue     = $('#addtoqueue').prop('checked');
            var targetSellPrice = parseFloat($('#targetsellpriceinput').val());
            var formData = {iid:parseInt(id),
                            maxPricePerShare:maxPerShare,
                            buyNumShares:numSharesWant,
                            addToQueueOnFailure:wantToQueue,
                            targetSellPrice:targetSellPrice};

            var message, header, type;

            postJSON('buyshares.action', formData,function(data) {
                if ( data.success ) {
                    gClosedialog.close();

                    //Update the money onscreen (it's okay if we don't buy anything because totalspent=0)
                    setUserMoney(getUserMoney()-data.totalSpent);
                    //FIXME: WARNING, Most of buy shares is all fucked up. We probably will only get QUEUED.NOMOREMONEY OR NOBUY.NOMOREMONEY or NOBUY.NOMORESHARES
                    setNumSharesForidea(id,data.numSharesFinal);
                    if ( data.result == 'OK' ) {
                        //Update the selling price onscreen
                        setSellingPriceIdea(id,targetSellPrice);
                        message =
                                '<span style="color: #6fc65d" class="glyphicon glyphicon-ok-circle"></span>&nbsp;<span style="color: #6fc65d">'+data.numSharesBought+'</span> shares foram compradas pelo total de <span style="color: #6fc65d">'+data.totalSpent+'</span> DEICoins.</span>'
                        message =
                                $('<div style="text-align:center;"><div style="font-size:40pt;"><span style="color: #6fc65d" class="glyphicon glyphicon-ok-circle"></span></div>&nbsp;<span style="color: #6fc65d">'+data.numSharesBought+'</span> shares foram compradas pelo total de <span style="color: #6fc65d">'+data.totalSpent+'</span> DEICoins.<br /></div>');
                        header =
                                '<div class="bootstrap-dialog-title">Opera��o conclu�da com sucesso!</div><div class="bootstrap-dialog-close-button" style="display: block;"><button class="close"  onclick="gClosedialog.close();"><span class="glyphicon glyphicon-remove"></span></button></div>';
                        type = BootstrapDialog.TYPE_SUCCESS;
                    } else if ( data.result == 'QUEUED.NOMOREMONEY' ) {
                        // Request got queued because we ran out of money
                        //Update the selling price onscreen
                        setSellingPriceIdea(id,targetSellPrice);
                        message =
                                $('<div style="text-align:center;"><div style="font-size:40pt;"><span style="color: #f0ad4e" class="glyphicon glyphicon-remove-circle"></span></div>&nbsp;<span style="color: #f0ad4e">'+data.numSharesBought+'</span> shares foram compradas pelo total de <span style="color: #f0ad4e">'+data.totalSpent+'</span> DEICoins.<br /> N�o houve dinheiro para comprar mais (apesar de haver shares).<br /><span style="color: #1500d2">As restantes ser�o compradas assim que poss�vel.</span></span></div>');
                        header =
                                '<div class="bootstrap-dialog-title">Nem todas as shares foram compradas</div><div class="bootstrap-dialog-close-button" style="display: block;"><button class="close"  onclick="gClosedialog.close();"><span class="glyphicon glyphicon-remove"></span></button></div>';
                        type = BootstrapDialog.TYPE_WARNING;
                    } else if ( data.result == 'QUEUED.NOMORESHARES' ) {
                        // Request got queued because there are no more shares (no more shares at all or at our desired price)
                        //Update the selling price onscreen
                        setSellingPriceIdea(id,targetSellPrice);
                        message =
                                $('<div style="text-align:center;"><div style="font-size:40pt;"><span style="color: #f0ad4e" class="glyphicon glyphicon-remove-circle"></span></div>&nbsp;<span style="color: #f0ad4e">'+data.numSharesBought+'</span> shares foram compradas pelo total de <span style="color: #f0ad4e">'+data.totalSpent+'</span> DEICoins.<br /> O n�mero de sahres pedido n�o est� dispon�vel.<br /><span style="color: #1500d2">As restantes ser�o compradas assim que poss�vel.</span></span></div>');
                        header =
                                '<div class="bootstrap-dialog-title">Nem todas as shares foram compradas (n�o havia shares suficientes)</div><div class="bootstrap-dialog-close-button" style="display: block;"><button class="close"  onclick="gClosedialog.close();"><span class="glyphicon glyphicon-remove"></span></button></div>';
                        type = BootstrapDialog.TYPE_WARNING;
                    } else if ( data.result == 'NOBUY.NOMOREMONEY' ) {
                        // Not enough money to buy all the required shares, so nothing bought at all
                        message =
                                $('<div style="text-align:center;"><div style="font-size:40pt;"><span style="color: #d2322d" class="glyphicon glyphicon-remove-circle"></span></div>&nbsp;<span style="color: #d2322d">Nenhuma compra foi feita.<br /> N�o h� dinheiro suficiente.<br /><span style="color: #1500d2">Por seu pedido, dada esta situa��o, a opera��o foi abortada.</span></span></div>');
                        header =
                                '<div class="bootstrap-dialog-title">Opera��o cancelada</div><div class="bootstrap-dialog-close-button" style="display: block;"><button class="close"  onclick="gClosedialog.close();"><span class="glyphicon glyphicon-remove"></span></button></div>';
                        type = BootstrapDialog.TYPE_DANGER;
                    } else if ( data.result == 'NOBUY.NOMORESHARES' ) {
                        // Not enough shares are available, so none bought at all
                        message =
                                $('<div style="text-align:center;"><div style="font-size:40pt;"><span style="color: #d2322d" class="glyphicon glyphicon-remove-circle"></span></div>&nbsp;<span style="color: #d2322d">Nenhuma compra foi feita.<br /> N�o h� shares suficientes.<br /><span style="color: #1500d2">Por seu pedido, dada esta situa��o, a opera��o foi abortada.</span></span></div>');
                        header =
                                '<div class="bootstrap-dialog-title">Opera��o cancelada</div><div class="bootstrap-dialog-close-button" style="display: block;"><button class="close"  onclick="gClosedialog.close();"><span class="glyphicon glyphicon-remove"></span></button></div>';
                        type = BootstrapDialog.TYPE_DANGER;
                    }

                    var dialog = new BootstrapDialog({
                        size: BootstrapDialog.SIZE_LARGE,
                        type: type,
                        message: message,
                        closable:true
                    });
                    gClosedialog = dialog;
                    dialog.realize();
                    dialog.getModalHeader().html(header);
                    dialog.getModalFooter().html('<button class="btn btn-default btn-lg" onclick="gClosedialog.close();">Terminado</button>');
                    dialog.open();

                } else {
                    alert("Server Internal Error...RMI is probably down!");
                }
                return true;
            });
        }

        function getMarketValue(id) {
            return parseFloat($(getMarketValueStr(id)).text()); //FIXME
        }

        function onTargetSellPriceChanged() {
            var m = $('#targetsellpriceinput');
            var modalsubmitbutton = $('#modalsubmitbutton');
            if (m.val() == '' || m.val() == 0 || !isValidPositiveNum(m.val())) {
                modalsubmitbutton.prop('disabled', true);
                return;
            }

            modalsubmitbutton.prop('disabled', false);
        }

        function buyShares(id) {
            var currentmoney = getUserMoney();

            var sellingPrice = getSellingPriceIdea(id);
            if (sellingPrice == -1)
                sellingPrice = getMarketValue(id);

            var numsharesarea =
                    '<div class="input-append"><span class="glyphicon glyphicon-chevron-right"></span>&nbsp; N�mero de shares a comprar:&nbsp;<input name="text" id="numshareswant" value="'+1+'" style="width:125px;" onkeyup="onNumSharesWantChanged('+id+');" /></div>';
            var maxpersharearea =
                    '<div class="input-append"><span class="glyphicon glyphicon-chevron-right"></span>&nbsp; M�ximo por share:&nbsp;<input name="text" id="maxpershareinput" value="'+currentmoney+'" style="width:125px;" onkeyup="onMaxWillingToBuyChanged();" /> DEICoins</div>';

            var targetsellpricearea =
                    '<div class="input-append"><span class="glyphicon glyphicon-chevron-right"></span>&nbsp; Pre�o de venda:&nbsp;<input name="text" id="targetsellpriceinput" value="'+sellingPrice+'" style="width:125px;" onkeyup="onTargetSellPriceChanged();" /> DEICoins/share</div>';

            var modalcheckbox =
                    '<div class="input-append"><span class="glyphicon glyphicon-chevron-right"></span>&nbsp;<input type="checkbox" id="addtoqueue" value="true" > Colocar pedido na fila se n�o for poss�vel satisfazer</input></div>';

            var message = function(dialogRef){
                var $message =
                        $("<div style='font-size:16pt'>Come�ou com <span style='color: #6fc65d'>"+getNumSharesForIdea(id)+"</span> shares</div>");
                $message.append(numsharesarea).append($('<div>&nbsp;</div>')).append($(maxpersharearea)).append($('<div>&nbsp;</div>')).append($(targetsellpricearea)).append($(modalcheckbox));

                return $message;
            }
            var dialog = new BootstrapDialog({
                size: BootstrapDialog.SIZE_LARGE,
                message: message,
                closable:true
            });
            gClosedialog = dialog;

            var button =
                    '<button class="btn btn-primary btn-lg" id="modalsubmitbutton" onclick="doBuyShares('+id+');"><span class="glyphicon glyphicon-cloud"></span><span style="margin:-9px; color: #dbd02b" class="glyphicon glyphicon-euro"></span> &nbsp; &nbsp;Comprar Shares</button>';
            var closebutton
                    = '<button class="btn btn-default btn-lg" onclick="gClosedialog.close();">Cancelar</button>';


            dialog.realize();

            dialog.getModalHeader().html('<div' +
                    ' class="bootstrap-dialog-title">Comprar Shares</div><div class="bootstrap-dialog-close-button" style="display: block;"><button class="close"  onclick="gClosedialog.close();"><span class="glyphicon glyphicon-remove"></span></button></div>');

            dialog.getModalFooter().html(button+closebutton);
            dialog.open();
        }
    </script>

</head>
<body>
<div id="wrapper">

    <!-- Sidebar -->
    <div id="sidebar-wrapper">
        <ul class="sidebar-nav">
            <li class="sidebar-brand"><a href="#">Team TransformadaZ</a></li>
            <li style="margin-top:100px;"><a
                    href="#"><span class="glyphicon glyphicon-home">&nbsp;Home</span><span style="color:black">
                _</span></a></li>
            <li style="background-color: black"><a href="listtopics.action"><span
                    class="glyphicon glyphicon-list">&nbsp;Ver
                T�picos</span><span style="color:black">_</span></a></li>
            <li><a
                    href="#"><span class="glyphicon glyphicon-eye-open">&nbsp;Watchlist</span><span
                    style="color:black">
                _</span></a></li>
            <li><a href="#"><span class="glyphicon glyphicon-cloud">&nbsp;As Minhas Ideias</span><span style="color:black">_</span></a></li>
            <li><a href="#"><span class="glyphicon glyphicon-bell">&nbsp;Mensagens</span><span style="color:black">_</span></a></li>
            <li><a href="#"><span class="glyphicon glyphicon-fire">&nbsp;Hall of Fame</span><span style="color:black">
                _</span></a></li>
            <li><a href="#"><span class="glyphicon glyphicon-search">&nbsp;Pesquisar T�picos</span><span
                    style="color:black">
                _</span></a></li>
            <li><a href="#"><span class="glyphicon glyphicon-search"  style="z-index:0"></span><span
                    class="glyphicon glyphicon-cloud" style="margin-left:-5px; z-index:1">&nbsp;Pesquisar
                Ideias</span><span
                    style="color:black">
                _</span></a> </li>

            <s:if test="#session.client.adminStatus">
                <li><a href="#"><span class="glyphicon glyphicon-wrench">&nbsp;Painel de Administrador</span><span
                        style="color:black">
                _</span></a></li>
            </s:if>
        </ul>
    </div>

    <!-- Page content -->
    <div id="page-content-wrapper">
        <div class="content-header">
            <div class="bs-header">
                <h1  style="margin-left: -10px;">Idea Broker</h1>
                <p> Your ideas. Our market. </p>
            </div>
            <nav class="navbar navbar-default navbar-static-top" role="navigation">
                <ul class="nav nav-pills nav-justified"  style="font-size: 18pt;">
                    <li><a href="#"><span class="glyphicon glyphicon-user"></span>&nbsp;
                        <s:property value="#session.client.username"/></a></li>
                    <li><a href="#" id="coins"><span class="glyphicon glyphicon-euro"></span>&nbsp;<span
                            id="currmoney"><s:property
                            value="#session.client.coins"/></span> DEICoins</a></li>
                    <li><a href="#" id="numNotifications"><span class="glyphicon
                     glyphicon-envelope"></span>&nbsp;<s:property value="#session.client.numNotifications"/> Novas
                        Mensagens</a></li>
                </ul>
            </nav>
        </div>
        <!-- Keep all page content within the page-content inset div! -->
        <div class="page-content inset" style=" margin: -20px">
            <div style="position:relative">
                <div id="clouds" style="position:relative; margin: -20px; margin-top: -40px; z-index: -20;">
                    <div class="cloud x1"></div>
                    <div class="cloud x2"></div>
                    <div class="cloud x3"></div>
                    <div class="cloud x4"></div>
                    <div class="cloud x5"></div>
                    <div class="cloud x2"></div>
                    <div class="cloud x1"></div>
                </div>
                <div style="position:absolute; top:0; left:0; width:100%">
                    <div class="row" style="margin:10px; margin-top:30px">

                        <div class="col-md-8 col-md-offset-2">
                            <h2 style="text-align:center"><s:property value="title" /></h2>
                            <div class="list-group text-center" style = "margin-top:25px;">
                                <s:iterator var="i" step="1" value="ideas">
                                    <div class="list-group-item" id="idea<s:property value="id" />"
                                            style = "margin-top:25px; padding-bottom:50px;">
                                        <%-- href="viewidea.action?iid=<s:property value="iid" />" --%>
                                        <h4
                                                class="list-group-item-heading"><s:property value="title" /></h4>
                                        <p class="list-group-item-text">
                                            <div style="height: 25px">
                                            <div style="float:right; white-space:nowrap;" id="ideatags<s:property
                                             value="id" />">
                                                <!-- Labels here -->
                                                    <span
                                                            <s:if test="top.percentOwned == 0.0">style="display:none"</s:if>
                                                            class="label label-info" id="ownidea<s:property value="id" />"><span
                                                            class="glyphicon glyphicon-ok"></span><span
                                                            id="numshares<s:property value="id" />"><s:property
                                                            value="numSharesOwned" /></span> shares (
                                                        <span
                                                                id="percentshares<s:property value="id" />">
                                                            <s:property
                                                            value="percentOwned" /></span>%)
                                                    </span>
                                                <!-- Watchlist Label -->
                                                    <span
                                                            <s:if test="!top.inWatchList">style="display:none"</s:if>
                                                            class="label label-success"
                                                            id="watchlistlabel<s:property value="id"/>"><span class="glyphicon glyphicon-eye-open"></span>Na Watchlist
                                                        <a href="#" type="button"
                                                           onclick="removeFromWatchlist(<s:property
                                                                value="id" />);"><span
                                                                class="glyphicon glyphicon-remove"></span></a> </span>

                                            </div>
                                            <div style="float:left; white-space:nowrap;" id="idealeftarea<s:property
                                             value="id" />">
                                                <!-- Left buttons -->
                                                <s:if test="!#session.client.adminStatus">
                                                    <!-- Set share price -->

                                                    <div class="input-append"
                                                         id="setsharepriceeditbox<s:property value="id" />"
                                                <s:if test="top.percentOwned == 0.0">style="display:none"</s:if>>

                                                        <span
                                                                class="glyphicon glyphicon-euro"></span>
                                                        Vender a
                                                        <input name="text" id="sellingprice<s:property value="id" />"
                                                               value="<s:property value="sellingPrice" />"
                                                               style="width:50px;"
                                                               onkeyup="sharePriceChanged(<s:property
                                                                       value="id" />);"/>
                                                         DEICoins/share <button
                                                            id="btnsellingprice<s:property value="id" />"
                                                            class="btn btn-success btn-sm"
                                                            ><span
                                                                class="glyphicon glyphicon-ok-sign"></span></button>
                                                    </div>
                                                </s:if>
                                            </div>
                                        </div>
                                            <div style="height: 45px">
                                                <div style="float:left; margin-top: 5px;" id="buttonsleft<s:property
                                                     value="id" />">
                                                    <a id="marketvaluebtn<s:property value="id" />"
                                                       class = "btn btn-info btn-sm"
                                                       type="button"><span
                                                            class="glyphicon glyphicon-tags"
                                                            ></span> Market Value:
                                                        <span
                                                    id="marketvalue<s:property value="id" />"><s:property
                                                                value="marketValue" /></span></a>
                                                </div>
                                                <div style="float:right" id="buttons<s:property
                                                     value="id" />">
                                                    <!-- Buttons here -->
                                                    <s:if test="#session.client.adminStatus">
                                                        <a id="takeover<s:property value="id" />"
                                                           href="#" type="button"
                                                           class="btn btn-success btn-sm"
                                                           onclick="takeover(<s:property value="id" />)">
                                                            <span class="glyphicon glyphicon-fire"></span> Takeover
                                                        </a>
                                                    </s:if><s:else>
                                                    <!-- Delete idea -->
                                                        <a id="removeidea<s:property value="id" />"
                                                          <s:if test="top.percentOwned != 100.0">style="display: none"</s:if>
                                                                href="#" type="button"
                                                           class="btn btn-danger btn-sm"
                                                           onclick="removeIdea(<s:property value="id" />)">
                                                            <span class="glyphicon glyphicon-remove"></span> Apagar Ideia
                                                        </a>

                                                    <!-- Buy shares-->

                                                        <!--data-toggle="modal" href="#myModal"-->
                                                        <a <s:if test="top.percentOwned == 100.0">style="display:none"</s:if>
                                                           type="button"
                                                           class="btn btn-primary btn-sm"
                                                           id="buyshares<s:property value="id" />"
                                                           onclick="buyShares(<s:property value="id" />);">
                                                            <span class="glyphicon glyphicon-cloud"></span><span
                                                                style="margin:-9px; color: #dbd02b"
                                                                class="glyphicon glyphicon-euro"></span>
                                                            &nbsp; &nbsp;Comprar Shares
                                                        </a>


                                                        <a
                                                                <s:if test="top.inWatchList">style="display:none"</s:if>
                                                                href="#" type="button"
                                                           class="btn btn-success btn-sm"
                                                           id="addtowatchlistbtn<s:property value="id" />"
                                                           onclick="addToWatchlist(<s:property value="id" />);">
                                                            <span class="glyphicon glyphicon-eye-open"></span> Adicionar
                                                            � Watchlist
                                                        </a>
                                                    </s:else>
                                                </div>
                                        </div>
                                            <%--Watchlist: <s:property value="inWatchList" />
                                            Owned: <s:property value="numSharesOwned" />
                                            Percent Owned: <s:property value="percentOwned" />--%>
                                            <div>
                                                <s:property value="body" />
                                                <hr/>
                                            </div>

                                        <!-- Topic hashtags -->
                                            <div style="float:left; min-height:40px;" id="ideatopics<s:property
                                             value="id" />">
                                                <s:iterator var="i" step="1" value="topics">
                                                    <p><a href="viewtopic.action?tid=<s:property value="id" />">

                                                        <span
                                                            class="label label-primary">
                                                         #<s:property value="title" />
                                                    </span></a></p>
                                                </s:iterator>
                                            </div>
                                    </div>
                                </s:iterator>
                            </div><%--
                            <s:iterator var="i" step="1" value="ideas">
                                <s:url action="viewidea" var="urlTag">
                                    <s:param name="iid" value="top.id" />
                                </s:url>
                                <s:a href="%{urlTag}">Ideia <s:property value="iid" /></s:a><br />

                                <s:property value="id" /><br />
                                <s:property value="title" /><br />
                            </s:iterator>--%>

                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>
<script src="bootstrap-3.0.2/dist/js/bootstrap.min.js"></script>
<script src="bootstrap-dialog.js"></script>
</body>
</html>
