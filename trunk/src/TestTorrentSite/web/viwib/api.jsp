<%@page import="java.util.Random"%>
<%@page contentType="text/json" pageEncoding="UTF-8"%><%
%><%
    /*
     * actions: login, keepalive, logout, search
     */
    
    String action = request.getParameter("action");
    if (action == null) {
        out.write("{\"error\":\"Missing action parameter.\"}");
        return;
    }
    
    if ("login".equals(action)) {
        /*
         * > un: username
         * > pw: password (plain text)
         * 
         * < ssid
         */
        String un = request.getParameter("un");
        String pw = request.getParameter("pw");
        if (un == null || "".equals(un)) {
            out.print("{\"error\":\"Invalid username or password.\"}");
            return;
        }
        if (!un.equals(pw)) {
            out.print("{\"error\":\"Invalid username or password.\"}");
            return;
        }
        out.print("{\"ssid\":\"123456789\"}");
        return;
        
        
    } else if ("keepalive".equals(action)) {
        /*
         * > ssid
         */
    } else if ("logout".equals(action)) {
        /*
         * > ssid
         */
    } else if ("search".equals(action)) {
        /*
         * > ssid
         * > s: search term
         * > cat: categoryId
         * > sort: sortCategory id
         * > desc: isDescendent
         * 
         * < results
         */
        String searchTerm = request.getParameter("s");
        String cat = request.getParameter("cat");
        String sort = request.getParameter("sort");
        boolean desc = "true".equals(request.getParameter("desc"));
        String pageStr = request.getParameter("page");
        int pageIndex = Integer.parseInt(pageStr);
        
        String res = "{";
        int totalCount = new Random().nextInt(1000) + 1000;
        res += "\"totalCount\": " + totalCount + ",";
        res += "\"pageIndex\": " + pageIndex + ",";
        res += "\"pageSize\": 25,";
        res += "\"adUrl\": null,";
        res += "\"results\": [";
        
        int pageCount = 25;
        if (totalCount / 25 == pageIndex) {
            pageCount = totalCount % 25;
        }
        
        for (int i = 25 * pageIndex; i < 25 * pageIndex + 25; i++) {
            res += "{";
            res += "\"id\": \"" + i + "\",";
            res += "\"name\": \"Torrent " + i + "\",";
            res += "\"description\": \"Torrent " + i + " description.\",";
            res += "\"torrentUrl\": \"http://localhost:8080/t" + i + ".torrent\",";
            res += "\"coverUrl\": \"http://localhost:8080/cover.jpg\",";
            res += "\"dateAdded\": \"06/11/2011\",";
            res += "\"seeders\": 1234,";
            res += "\"leechers\": 56,";
            res += "\"downloaded\": 12345,";
            res += "\"size\": \"700 MB\",";
            res += "\"categoryId\": \"11\",";
            
            res += "}";
            if (i < 25 * pageIndex + 24) {
                res += ",";
            }
        }
        
        res += "]";
        res += "}";
        out.print(res);
    } else if ("getSeedProgress".equals(action)) {
        out.print("{\"progress\": 1.0}");
    }
%>
