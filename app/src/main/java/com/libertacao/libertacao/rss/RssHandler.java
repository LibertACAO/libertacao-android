package com.libertacao.libertacao.rss;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2014 Shirwa Mohamed <shirwa99@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Code from: https://github.com/ShirwaM/Simplistic-RSS
public class RssHandler extends DefaultHandler {
    private List<RssItem> rssItemList;
    private RssItem currentItem;
    private boolean parsingTitle;
    private boolean parsingLink;
    private boolean parsingDescription;

    public RssHandler() {
        //Initializes a new ArrayList that will hold all the generated RSS items.
        rssItemList = new ArrayList<>();
    }

    public List<RssItem> getRssItemList() {
        return rssItemList;
    }


    //Called when an opening tag is reached, such as <item> or <title>
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch (qName) {
            case "entry":
                currentItem = new RssItem();
                break;
            case "title":
                parsingTitle = true;
                break;
            case "link":
                parsingLink = true;
                break;
            case "summary":
                parsingDescription = true;
                break;
        }
    }

    //Called when a closing tag is reached, such as </item> or </title>
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case "entry":
                //End of an item so add the currentItem to the list of items.
                rssItemList.add(currentItem);
                currentItem = null;
                break;
            case "title":
                parsingTitle = false;
                break;
            case "link":
                parsingLink = false;
                break;
            case "summary":
                parsingDescription = false;
                break;
        }
    }

    //Goes through character by character when parsing whats inside of a tag.
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (currentItem != null) {
            if (parsingTitle) {
                //If parsingTitle is true, then that means we are inside a <title> tag so the text is the title of an item.
                currentItem.setTitle(new String(ch, start, length));
            }
            // TODO: it is not parsing links!
            else if (parsingLink) {
                //If parsingLink is true, then that means we are inside a <link> tag so the text is the link of an item.
                currentItem.setLink(new String(ch, start, length));
            }
            else if (parsingDescription) {
                //If parsingDescription is true, then that means we are inside a <description> tag so the text is the description of an item.
                currentItem.setDescription(new String(ch, start, length));
            }
        }
    }
}
