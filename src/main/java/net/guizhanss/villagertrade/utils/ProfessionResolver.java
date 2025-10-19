package net.guizhanss.villagertrade.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Villager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfessionResolver {
    private static final Map<String, Villager.Profession> LOOKUP;

    static {
        Map<String, Villager.Profession> m = new HashMap<>();

        for (Villager.Profession p : Registry.VILLAGER_PROFESSION) {
            String full = p.getKey().toString();
            String path = p.getKey().getKey();

            m.put(full, p);
            m.put(path, p);

            m.put(path.replace('_', ' '), p);
            m.put(path.replace('_', '-'), p);
        }

        Map<String,String> aliases = Map.of(
            "weapon smith", "weaponsmith",
            "tool smith",   "toolsmith",
            "leather worker", "leatherworker",
            "stone mason",  "mason"
        );
        aliases.forEach((alias, target) -> {
            Villager.Profession p = m.get(target);
            if (p != null) m.put(alias, p);
        });

        LOOKUP = Collections.unmodifiableMap(m);
    }

    public static Villager.Profession resolve(String input) {
        if (input == null) return null;

        String s = input.trim().toLowerCase(Locale.ROOT);

        NamespacedKey key = NamespacedKey.fromString(s);
        if (key != null) {
            Villager.Profession p = Registry.VILLAGER_PROFESSION.get(key);
            if (p != null) return p;
            s = key.getKey();
        }

        Villager.Profession p = LOOKUP.get(s);
        if (p != null) return p;

        String compact = s.replace(" ", "").replace("-", "").replace("_", "");
        for (Map.Entry<String, Villager.Profession> e : LOOKUP.entrySet()) {
            String k = e.getKey().replace(" ", "").replace("-", "").replace("_", "");
            if (k.equals(compact)) return e.getValue();
        }

        return null;
    }
}
