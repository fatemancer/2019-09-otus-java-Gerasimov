package ru.otus.json.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StrangeClass {
    byte[] values = {2, 55, 14};
    char[] password = {'p', 'a', 's', 's'};
    char[] emojis = "\uD83E\uDD14".toCharArray();
}
