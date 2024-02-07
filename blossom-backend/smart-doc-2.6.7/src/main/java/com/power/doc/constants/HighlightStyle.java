/*
 * smart-doc https://github.com/smart-doc-group/smart-doc
 *
 * Copyright (C) 2018-2023 smart-doc
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.power.doc.constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.power.common.util.StringUtil;

/**
 * @author jitmit 2020/11/16
 */
public class HighlightStyle {

    public static final String DEFAULT_STYLE = "github";

    private static final List<String> DARK_STYLE;

    private static final List<String> LIGHT_STYLE;

    /**
     * key is style,value is color
     */
    private static final Map<String, String> background = new HashMap<>();

    static {
        LIGHT_STYLE = Arrays.asList(
            DEFAULT_STYLE,
            "a11y-light",
            "arduino-light",
            "ascetic",
            "atelier-cave-light",
            "atelier-dune-light",
            "atelier-estuary-light",
            "atelier-forest-light",
            "atelier-heath-light",
            "atelier-lakeside-light",
            "atelier-plateau-light",
            "atelier-savanna-light",
            "atelier-seaside-light",
            "atelier-sulphurpool-light",
            "atom-one-light",
            "color-brewer",
            "docco",
            "github-gist",
            "googlecode",
            "grayscale",
            "gruvbox-light",
            "idea",
            "isbl-editor-light",
            "kimbie.light",
            "lightfair",
            "magula",
            "mono-blue",
            "nnfx",
            "paraiso-light",
            "purebasic",
            "qtcreator_light",
            "routeros",
            "school-book",
            "solarized-light",
            "tomorrow",
            "vs",
            "xcode"
        );

    }

    static {
        DARK_STYLE = Arrays.asList(
            "a11y-dark",
            "agate",
            "an-old-hope",
            "androidstudio",
            "arta",
            "atelier-cave-dark",
            "atelier-dune-dark",
            "atelier-estuary-dark",
            "atelier-forest-dark",
            "atelier-heath-dark",
            "atelier-lakeside-dark",
            "atelier-plateau-dark",
            "atelier-savanna-dark",
            "atelier-seaside-dark",
            "atelier-sulphurpool-dark",
            "atom-one-dark-reasonable",
            "atom-one-dark",
            "brown-paper",
            "codepen-embed",
            "darcula",
            "dark",
            "default",
            "dracula",
            "far",
            "foundation",
            "gml",
            "gradient-dark",
            "gruvbox-dark",
            "hopscotch",
            "hybrid",
            "ir-black",
            "isbl-editor-dark",
            "kimbie.dark",
            "lioshi",
            "monokai",
            "monokai-sublime",
            "night-owl",
            "nnfx-dark",
            "nord",
            "obsidian",
            "ocean",
            "paraiso-dark",
            "pojoaque",
            "qtcreator_dark",
            "railscasts",
            "rainbow",
            "shades-of-purple",
            "solarized-dark",
            "srcery", "sunburst",
            "tomorrow-night",
            "tomorrow-night-blue",
            "tomorrow-night-bright",
            "tomorrow-night-eighties",
            "vs2015",
            "xt256",
            "zenburn"
        );
    }

    static {
        background.put("a11y-dark", "#2b2b2b");
        background.put("agate", "#333");
        background.put("androidstudio", "#282b2e");
        background.put("atom-one-light", "#fafafa");
        background.put("an-old-hope", "#1c1d21");
        background.put("arta", "#222");
        background.put("atelier-cave-dark", "#19171c");
        background.put("atelier-cave-light", "#efecf4");
        background.put("atelier-dune-dark", "#20201d");
        background.put("atelier-dune-light", "#fefbec");
        background.put("atelier-estuary-dark", "#22221b");
        background.put("atelier-estuary-light", "#f4f3ec");
        background.put("atelier-forest-dark", "#1b1918");
        background.put("atelier-forest-light", "#f1efee");
        background.put("atelier-heath-dark", "#1b181b");
        background.put("atelier-heath-light", "#f7f3f7");
        background.put("atelier-lakeside-dark", "#161b1d");
        background.put("atelier-lakeside-light", "#ebf8ff");
        background.put("atelier-plateau-dark", "#1b1818");
        background.put("atelier-plateau-light", "#f4ecec");
        background.put("atelier-savanna-dark", "#171c19");
        background.put("atelier-savanna-light", "#ecf4ee");
        background.put("atelier-seaside-dark", "#131513");
        background.put("atelier-seaside-light", "#f4fbf4");
        background.put("atelier-sulphurpool-dark", "#202746");
        background.put("atelier-sulphurpool-light", "#f5f7ff");
        background.put("atom-one-dark", "#282c34");
        background.put("atom-one-dark-reasonable", "#282c34");
        background.put("codepen-embed", "#222");
        background.put("darcula", "#2b2b2b");
        background.put("dark", "#444");
        background.put("default", "#F0F0F0");
        background.put("docco", "#f8f8ff");
        background.put("dracula", "#282a36");
        background.put("far", "#000080");
        background.put("foundation", "#eee");
        background.put("github", "#f8f8f8");
        background.put("gml", "#222222");
        background.put("gradient-dark", "linear-gradient(166deg, rgba(80,31,122,1) 0%, rgba(40,32,179,1) 80%)");
        background.put("gruvbox-dark", "#282828");
        background.put("gruvbox-light", "#fbf1c7");
        background.put("hopscotch", "#322931");
        background.put("hybrid", "#1d1f21");
        background.put("ir-black", "#000");
        background.put("isbl-editor-dark", "#404040");
        background.put("kimbie.dark", "#221a0f");
        background.put("kimbie.light", "#fbebd4");
        background.put("lioshi", "#303030");
        background.put("magula", "#f4f4f4");
        background.put("mono-blue", "#eaeef3");
        background.put("monokai", "#272822");
        background.put("monokai-sublime", "#23241f");
        background.put("night-owl", "#011627");
        background.put("nnfx-dark", "#333");
        background.put("nord", "#2E3440");
        background.put("obsidian", "#282b2e");
        background.put("ocean", "#2b303b");
        background.put("paraiso-dark", "#2f1e2e");
        background.put("paraiso-light", "#e7e9db");
        background.put("purebasic", "#FFFFDF");
        background.put("qtcreator_dark", "#000000");
        background.put("railscasts", "#232323");
        background.put("rainbow", "#474949");
        background.put("routeros", "#f0f0f0");

        background.put("shades-of-purple", "#2d2b57");
        background.put("solarized-dark", "#002b36");
        background.put("solarized-light", "#fdf6e3");
        background.put("srcery", "#1C1B19");
        background.put("sunburst", "#000");
        background.put("tomorrow-night", "#1d1f21");
        background.put("tomorrow-night-blue", "#002451");
        background.put("tomorrow-night-bright", "black");
        background.put("tomorrow-night-eighties", "#2d2d2d");
        background.put("xt256", "#000");
        background.put("vs2015", "#1E1E1E");
        background.put("zenburn", "#3f3f3f");
    }


    /**
     * Randomly select a light style
     *
     * @param random Random
     * @return String of random
     */
    public static String randomLight(Random random) {
        return LIGHT_STYLE.get(random.nextInt(LIGHT_STYLE.size()));
    }

    /**
     * Randomly select a dark style
     *
     * @param random Random
     * @return String of random
     */
    public static String randomDark(Random random) {
        return DARK_STYLE.get(random.nextInt(DARK_STYLE.size()));
    }

    /**
     * Randomly select a style
     *
     * @param random Random
     * @return String of random
     */
    public static String randomAll(Random random) {
        if (random.nextBoolean()) {
            return randomLight(random);
        } else {
            return randomDark(random);
        }
    }

    public static String getBackgroundColor(String style) {
        String color = background.get(style);
        if (StringUtil.isNotEmpty(color)) {
            return color;
        }
        return "#f7f7f8";
    }

    /**
     * Does the highlight style exist?
     *
     * @param style Highlight style
     * @return boolean
     */
    public static boolean containsStyle(String style) {
        return background.containsKey(style);
    }
}
