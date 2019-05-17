package dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProxyMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    public final UserDto sender;
    public final String msg;
    public final long time;
}
